/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.book;

import org.apache.wicket.Session;
import org.apache.wicket.util.string.Strings;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.entity.StringCultureId;
import org.vetcontrol.book.annotation.BookReference;
import org.vetcontrol.book.annotation.MappedProperty;
import org.vetcontrol.book.annotation.ValidProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Table;
import org.apache.wicket.util.string.interpolator.VariableInterpolator;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.book.annotation.UIType;
import org.vetcontrol.book.annotation.ViewLength;

/**
 *
 * @author Artem
 */
public class BeanPropertyUtil {

    public static final Class[] SIMPLE_TYPES = {
        int.class, byte.class, short.class, long.class, double.class, float.class, boolean.class,
        Integer.class, Byte.class, Short.class, Long.class, Double.class, Float.class, Boolean.class,
        String.class, Date.class, List.class, Set.class, Map.class
    };

    public static final Class[] PRIMITIVES = {
        int.class, byte.class, short.class, long.class, double.class, float.class, boolean.class,
        Integer.class, Byte.class, Short.class, Long.class, Double.class, Float.class, Boolean.class,
        String.class, Date.class
    };

    public static final Class[] NUMBER_TYPES = {
        int.class, byte.class, short.class, long.class, double.class, float.class,
        Integer.class, Byte.class, Short.class, Long.class, Double.class, Float.class
    };

    private static class EntityMetadata {

        String tableName;

        /**
         * Property name to property map.
         */
        LinkedHashMap<String, Property> propertyNamesMap;
    }

    private static final ConcurrentHashMap<Class, EntityMetadata> metadataCache = new ConcurrentHashMap<Class, EntityMetadata>();

    private static void initMetadata(EntityMetadata metadata) {
        if (metadata.propertyNamesMap == null) {
            metadata.propertyNamesMap = newPropertyMap();
        }
    }

    private static EntityMetadata newEntityMetadata() {
        return new EntityMetadata();
    }

    private static LinkedHashMap<String, Property> newPropertyMap() {
        return new LinkedHashMap<String, Property>();
    }

    public static Collection<Property> getProperties(Class<?> entityClass) {
        //check cache for the presence of metadata:
        EntityMetadata metadata = metadataCache.get(entityClass);
        if (metadata != null) {
            return metadata.propertyNamesMap.values();
        } else {
            //create and initialize new metadata:
            metadata = newEntityMetadata();
            initMetadata(metadata);

            metadata.tableName = getTableName(entityClass);

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(entityClass);
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
                    property.setSurroundingClass(entityClass);

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
                                    property.setColumnName(column.name());
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
                                    property.setColumnName(joinColumn.name());
                                }

                                if (annotation.annotationType().equals(ViewLength.class)) {
                                    ViewLength viewLength = (ViewLength) annotation;
                                    property.setViewLength(viewLength.value());
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

                for (Property prop : filtered) {
                    for (Property p : filtered) {
                        if (p.getName().equals(prop.getLocalizationForeignKeyProperty())) {
                            prop.setColumnName(p.getColumnName());
                            break;
                        }
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

                LinkedHashMap<String, Property> propertyMap = metadata.propertyNamesMap;
                for (Property prop : result) {
                    propertyMap.put(prop.getName(), prop);
                }

                //check whether cache already contains bean class key. This could occur in other thread while current thread was in processing.
                if (!metadataCache.containsKey(entityClass)) {
                    metadataCache.put(entityClass, metadata);
                }

                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addLocalization(Object book, List<Locale> supportedLocales) {
        try {
            for (Property prop : getProperties(book.getClass())) {
                if (prop.isLocalizable()) {
                    List<StringCulture> strings = (List<StringCulture>) getPropertyValue(book, prop.getName());

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
                    setPropertyValue(book, prop.getName(), strings);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Property '" + propertyName + "' was not found in type " + target.getClass());
    }

    public static String getPropertyAsString(Object propertyValue, Property property, Locale systemLocale) {
        if (propertyValue != null) {
            if (isDateType(property.getType())) {
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

    public static String getLocalizablePropertyAsStringInLocale(List<StringCulture> propertyValue, Locale locale) {
        String asString = "";
        List<StringCulture> list = (List<StringCulture>) propertyValue;
        for (StringCulture culture : list) {
            if (new Locale(culture.getId().getLocale()).getLanguage().equalsIgnoreCase(locale.getLanguage())) {
                if (!Strings.isEmpty(culture.getValue())) {
                    asString = culture.getValue();
                    break;
                }
            }
        }

        return asString;
    }

    public static Property getPropertyByName(Class entityClass, String propertyName) {
        for (Property prop : getProperties(entityClass)) {
            if (prop.getName().equals(propertyName)) {
                return prop;
            }
        }
        return null;
    }

    public static void setPropertyValue(Object target, String propertyName, Object value) {
        boolean isFound = false;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                if (prop.getName().equals(propertyName)) {
                    isFound = true;
                    prop.getWriteMethod().invoke(target, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!isFound) {
            throw new RuntimeException("Property '" + propertyName + "' was not found in type " + target.getClass());
        }
    }

    public static <T> String applyPattern(String pattern, final T book, final Locale systemLocale) {
        return new VariableInterpolator(pattern, true) {

            @Override
            protected String getValue(String variableName) {
                Object propertyValue = getPropertyValue(book, variableName);
                return getPropertyAsString(propertyValue, getPropertyByName(book.getClass(), variableName), systemLocale);
            }
        }.toString();
    }

    private static int bookHash(Object book) {
        int hash = 17;
        int multiplier = 37;

        for (Property prop : getProperties(book.getClass())) {
            Class propType = prop.getType();
            if (isPrimitive(propType)) {
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
                    Object ID = getId(value);
                    if (ID != null) {
                        hash = hash * multiplier + ID.hashCode();
                    }
                }
            }
        }
        return hash;
    }

    private static int stringCultureHash(StringCulture culture) {
        return culture.getValue() == null ? 0 : culture.getValue().hashCode();
    }

    public static BookHash hash(Object book) {
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
    }

    public static void updateVersionIfNecessary(Object book, BookHash initialHash) {
        Date updated = DateUtil.getCurrentDate();

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

        BookHash newHash = hash(book);
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
    }

    public static String getVersionPropertyName() {
        return "updated";
    }

    public static boolean isNewBook(Object book) {
        return getId(book) == null;
    }

    public static void clearBook(Object book) {
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
    }

    public static String getDisabledPropertyName() {
        return "disabled";
    }

    public static String getDisabledColumnName() {
        return "disabled";
    }

    public static String getTableName(Class<?> entityClass) {
        //check cache for metadata:
        EntityMetadata metadata = metadataCache.get(entityClass);
        if (metadata != null) {
            return metadata.tableName;
        } else {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if (tableAnnotation == null) {
                throw new IllegalArgumentException("Entity definition must specify table name. Entity class: " + entityClass);
            }
            return tableAnnotation.name();
        }
    }

    public static boolean isPrimitive(Class type) {
        boolean isPrimitive = false;
        for (Class primitive : PRIMITIVES) {
            if (primitive.isAssignableFrom(type)) {
                isPrimitive = true;
                break;
            }
        }
        return isPrimitive;
    }

    public static boolean isNumberType(Class type) {
        boolean isNumberType = false;
        for (Class numberType : NUMBER_TYPES) {
            if (numberType.isAssignableFrom(type)) {
                isNumberType = true;
                break;
            }
        }
        return isNumberType;
    }

    public static final boolean isBoolType(Class type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    public static final boolean isDateType(Class type) {
        return Date.class.isAssignableFrom(type);
    }

    public static Long getId(Object book) {
        return (Long) getPropertyValue(book, "id");
    }
}
