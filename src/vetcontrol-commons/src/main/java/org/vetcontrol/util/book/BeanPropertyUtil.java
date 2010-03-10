/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.book;

import org.apache.wicket.Session;
import org.apache.wicket.util.string.Strings;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.entity.StringCultureId;
import org.vetcontrol.util.book.entity.annotation.BookReference;
import org.vetcontrol.util.book.entity.annotation.MappedProperty;
import org.vetcontrol.util.book.entity.annotation.ValidProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.*;
import org.apache.wicket.util.string.interpolator.VariableInterpolator;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.util.book.entity.annotation.UIType;

/**
 *
 * @author Artem
 */
public class BeanPropertyUtil {

    public static final List<Class> SIMPLE_TYPIES = Arrays.asList(new Class[]{
                int.class, byte.class, short.class, long.class, double.class, float.class, boolean.class,
                Integer.class, Byte.class, Short.class, Long.class, Double.class, Float.class, Boolean.class,
                String.class, Date.class, List.class, Set.class, Map.class
            });
    public static final List<Class> PRIMITIVES = Arrays.asList(new Class[]{
                int.class, byte.class, short.class, long.class, double.class, float.class, boolean.class,
                Integer.class, Byte.class, Short.class, Long.class, Double.class, Float.class, Boolean.class,
                String.class, Date.class
            });

    public static List<Property> getProperties(Class<?> beanClass) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        List<Property> filtered = new ArrayList<Property>();
        List<String> excludes = new ArrayList<String>();
        for (PropertyDescriptor prop : props) {

            boolean validProperty = true;

            if (prop.getName().equals("class")) {
                validProperty = false;
            }

            Property property = new Property();
            property.setName(prop.getDisplayName());
            property.setType(prop.getPropertyType());
            property.setSurroundingClass(beanClass);

            if (prop.getReadMethod() != null
                    && Modifier.isPublic(prop.getReadMethod().getModifiers())
                    && !Modifier.isAbstract(prop.getReadMethod().getModifiers())) {

                property.setReadable(true);

                if (prop.getWriteMethod() == null
                        || !Modifier.isPublic(prop.getWriteMethod().getModifiers())
                        || Modifier.isAbstract(prop.getWriteMethod().getModifiers())) {

                    property.setWritable(false);
                    validProperty = false;
                } else {
                    property.setWritable(true);
                    for (Annotation annotation : prop.getReadMethod().getAnnotations()) {
                        if (annotation.annotationType().equals(ValidProperty.class)) {
                            validProperty = ((ValidProperty) annotation).value();
                        }

                        if (annotation.annotationType().equals(Id.class)) {
                            validProperty = false;
                        }

                        if (annotation.annotationType().equals(Column.class)) {
                            Column column = (Column) annotation;
                            property.setNullable(column.nullable());
                            property.setLength(column.length());
                        }

                        if (annotation.annotationType().equals(MappedProperty.class)) {
                            property.setLocalizable(true);
                            String excludeProp = ((MappedProperty) annotation).value();
                            property.setLocalizationForeignKeyProperty(excludeProp);
                            excludes.add(excludeProp);
                        }


                        if (annotation.annotationType().equals(BookReference.class)) {
                            property.setBookReference(true);
                            BookReference bookReference = (BookReference) annotation;
                            String referencedField = bookReference.referencedProperty();
                            property.setReferencedField(referencedField);

                            UIType uIType = bookReference.uiType();
                            property.setUiType(uIType);

                            String pattern = bookReference.pattern();
                            property.setBookReferencePattern(pattern);
                        }

                        if (annotation.annotationType().equals(JoinColumn.class)) {
                            JoinColumn joinColumn = (JoinColumn) annotation;
                            property.setNullable(joinColumn.nullable());
                        }
                    }
                }
            } else {
                property.setReadable(false);
                validProperty = false;
            }

            if (validProperty) {
                filtered.add(property);
            }
        }

        List<Property> result = new ArrayList<Property>();

        for (Property prop : filtered) {
            boolean isInclude = true;
            for (String ex : excludes) {
                if (prop.getName().equals(ex)) {
                    isInclude = false;
                    break;
                }
            }
            if (isInclude) {
                result.add(prop);
            }
        }

        Collections.sort(result, new Comparator<Property>() {

            @Override
            public int compare(Property o1, Property o2) {
                if (o1.isLocalizable()) {
                    if (o2.isLocalizable()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    return -1;
                }
            }
        });

        return result;
    }

    public static void addLocalization(Object bookEntry, List<Locale> supportedLocales) {
        try {
            for (Property prop : getProperties(bookEntry.getClass())) {
                if (prop.isLocalizable()) {
                    List<StringCulture> strings = (List<StringCulture>) getPropertyValue(bookEntry, prop.getName());

                    List<StringCulture> toAdd = new ArrayList<StringCulture>();
                    if (strings.size() < supportedLocales.size()) {
                        Long stringId = null;
                        if (strings.size() > 0) {
                            stringId = strings.get(0).getId().getId();
                        }
                        for (Locale locale : supportedLocales) {
                            boolean add = true;
                            for (StringCulture sc : strings) {
                                if (new Locale(sc.getId().getLocale()).getLanguage().equalsIgnoreCase(locale.getLanguage())) {
                                    add = false;
                                    break;
                                }
                            }
                            if (add) {
                                StringCultureId cultureId = new StringCultureId(locale.getLanguage());
                                if (stringId != null) {
                                    cultureId.setId(stringId);
                                }
                                StringCulture culture = new StringCulture(cultureId, "");
                                toAdd.add(culture);
                            }
                        }
                    }
                    strings.addAll(toAdd);
                    Collections.sort(strings, new Comparator<StringCulture>() {

                        @Override
                        public int compare(StringCulture o1, StringCulture o2) {
                            return o1.getId().getLocale().compareToIgnoreCase(o2.getId().getLocale());
                        }
                    });
                    setPropertyValue(bookEntry, prop.getName(), strings);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static Map<PropertyDescriptor, PropertyDescriptor> getMappedProperties(Class bookType) throws IntrospectionException {
        //List prop to long prop
        Map<PropertyDescriptor, PropertyDescriptor> mappedProperties = new HashMap<PropertyDescriptor, PropertyDescriptor>();
        BeanInfo beanInfo = Introspector.getBeanInfo(bookType);
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor prop : props) {
            Method getter = prop.getReadMethod();
            MappedProperty mp = getter.getAnnotation(MappedProperty.class);
            if (mp != null && prop.getPropertyType().equals(List.class)) {
                String mappedProperty = mp.value();
                for (PropertyDescriptor prop2 : props) {
                    if (prop2.getName().equals(mappedProperty)) {
                        mappedProperties.put(prop, prop2);
                    }
                }
            }
        }
        return mappedProperties;
    }

    public static Object getPropertyValue(Object target, String propertyName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                if (prop.getName().equals(propertyName)) {
                    return prop.getReadMethod().invoke(target);
                }
            }
            throw new RuntimeException("Property '" + propertyName + "' was not found in type " + target.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String getPropertyAsString(Object propertyValue, Property property, Locale systemLocale) {
        if (propertyValue != null) {
            if (Date.class.isAssignableFrom(property.getType())) {
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Session.get().getLocale());
                return dateFormat.format((Date) propertyValue);
            } else if (property.isLocalizable()) {
                return getLocalizablePropertyAsString((List<StringCulture>) propertyValue, systemLocale, "");
            } else if (property.isBookReference()) {
//                if (property.getUiType().equals(UIType.AUTO_COMPLETE) && !Strings.isEmpty(property.getBookReferencePattern())) {
//                    return applyPattern(property.getBookReferencePattern(), propertyValue, systemLocale);
//                } else {
                Object referencedBook = propertyValue;
                String referencedField = property.getReferencedField();
                Object value = null;
                try {
                    value = getPropertyValue(referencedBook, referencedField);
                } catch (Exception e) {
                    //TODO: remove it after testing.
                    throw new RuntimeException(e);
                }
                return getPropertyAsString(value, getPropertyByName(referencedBook.getClass(), referencedField), systemLocale);
//                }

            } else {
                return propertyValue.toString();
            }
        }
        return "";
    }

    public static String getLocalizablePropertyAsString(List<StringCulture> propertyValue, Locale systemLocale, String defaultValue) {
        String asString = defaultValue == null ? "" : defaultValue;
        Locale currentLocale = Session.get().getLocale();
        List<StringCulture> list = (List<StringCulture>) propertyValue;
        boolean finded = false;
        //try to find in current locale.
        for (StringCulture culture : list) {
            if (new Locale(culture.getId().getLocale()).getLanguage().equalsIgnoreCase(currentLocale.getLanguage())) {
                if (!Strings.isEmpty(culture.getValue())) {
                    finded = true;
                    asString = culture.getValue();
                    break;
                }
            }
        }
        if (!finded) {
            //try to find in system locale.
            for (StringCulture culture : list) {
                if (new Locale(culture.getId().getLocale()).getLanguage().equalsIgnoreCase(systemLocale.getLanguage())) {
                    if (!Strings.isEmpty(culture.getValue())) {
                        finded = true;
                        asString = culture.getValue();
                        break;
                    }
                }
            }
        }
        if (!finded) {
            if (!list.isEmpty()) {
                asString = list.get(0).getValue();
            }
        }

        return asString;
    }

    public static Property getPropertyByName(Class beanClass, String propertyName) {
        try {
            for (Property prop : getProperties(beanClass)) {
                if (prop.getName().equals(propertyName)) {
                    return prop;
                }
            }
            return null;
        } catch (IntrospectionException e) {
            //TODO: remove it after tests.
            throw new RuntimeException(e);
        }
    }

    public static void setPropertyValue(Object target, String propertyName, Object value) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        boolean isFound = false;
        for (PropertyDescriptor prop : props) {
            if (prop.getName().equals(propertyName)) {
                isFound = true;
                prop.getWriteMethod().invoke(target, value);
            }
        }
        if (!isFound) {
            throw new RuntimeException("Property '" + propertyName + "' was not found in type " + target.getClass());
        }
    }

    public static <T> String applyPattern(String pattern, final T book, final Locale systemLocale) {
        return new VariableInterpolator(pattern, true) {

            @Override
            protected String getValue(String variableName) {
                try {
                    Object propertyValue = getPropertyValue(book, variableName);
                    return getPropertyAsString(propertyValue, getPropertyByName(book.getClass(), variableName), systemLocale);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }.toString();
    }

    private static int bookHash(Object book) {
        try {
            int hash = 17;
            int multiplier = 37;

            for (Property prop : getProperties(book.getClass())) {
                Class propType = prop.getType();
                boolean isPrimitive = false;
                for (Class primitive : PRIMITIVES) {
                    if (primitive.isAssignableFrom(propType)) {
                        isPrimitive = true;
                        break;
                    }
                }
                if (isPrimitive) {
                    //simple property
                    Object value = getPropertyValue(book, prop.getName());
                    if (value != null) {
                        hash = hash * multiplier + value.hashCode();
                    }
                } else if (prop.isLocalizable()) {
                    //do nothing.
                } else if (prop.isBookReference()) {

                    Object value = getPropertyValue(book, prop.getName());
                    if (value != null) {
                        Object ID = getPropertyValue(value, "id");
                        if (ID != null) {
                            hash = hash * multiplier + ID.hashCode();
                        }
                    }
                }
            }
            return hash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int stringCultureHash(StringCulture culture) {
        return culture.getValue() == null ? 0 : culture.getValue().hashCode();
    }

    public static BookHash hash(Object book) {
        try {
            BookHash bookHash = new BookHash();
            bookHash.setBookHash(bookHash(book));
            for (Property prop : getProperties(book.getClass())) {
                if (prop.isLocalizable()) {
                    List<StringCulture> strings = (List<StringCulture>) getPropertyValue(book, prop.getName());
                    if (strings != null) {
                        for (StringCulture stringCulture : strings) {
                            bookHash.addStringCultureHash(prop.getName(), stringCulture.getId().getLocale(), stringCultureHash(stringCulture));
                        }
                    }
                }
            }
            return bookHash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateVersionIfNecessary(Object book, BookHash initialHash) {
        try {
            Date updated = DateUtil.getCurrentDate();

            BookHash newHash = hash(book);

            if (isNewBook(book)) {
                setPropertyValue(book, getVersionPropertyName(), updated);
                for (Property prop : getProperties(book.getClass())) {
                    if (prop.isLocalizable()) {
                        List<StringCulture> strings = (List<StringCulture>) getPropertyValue(book, prop.getName());
                        if (strings != null) {
                            for (StringCulture stringCulture : strings) {
                                stringCulture.setUpdated(updated);
                            }
                        }
                    }
                }
                return;
            }

            if (!newHash.getBookHash().equals(initialHash.getBookHash())) {
                setPropertyValue(book, getVersionPropertyName(), updated);
            }

            for (String propName : newHash.getStringCultureHashes().keySet()) {
                for (String locale : newHash.getStringCultureHashes().get(propName).keySet()) {
                    Integer newStringCultureHash = newHash.getStringCultureHashes().get(propName).get(locale);
                    Integer initialStringCultureHash = initialHash.getStringCultureHashes().get(propName).get(locale);
                    if (!newStringCultureHash.equals(initialStringCultureHash)) {
                        List<StringCulture> strings = (List<StringCulture>) getPropertyValue(book, propName);
                        for (StringCulture culture : strings) {
                            if (culture.getId().getLocale().equals(locale) || culture.getUpdated() == null) {
                                culture.setUpdated(updated);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersionPropertyName() {
        return "updated";
    }

    public static boolean isNewBook(Object book) {
        try {
            return getPropertyValue(book, "id") == null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearBook(Object book) {
        try {
            setPropertyValue(book, "id", null);
            for (Property prop : getProperties(book.getClass())) {
                if (prop.isLocalizable()) {
                    List<StringCulture> strings = (List<StringCulture>) getPropertyValue(book, prop.getName());
                    if (strings != null) {
                        for (StringCulture stringCulture : strings) {
                            stringCulture.getId().setId(null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDisabledPropertyName() {
        return "disabled";
    }
}
