/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.web;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import org.apache.wicket.Session;
import org.apache.wicket.util.string.Strings;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.model.StringCultureId;
import org.vetcontrol.information.util.model.annotation.BookReference;
import org.vetcontrol.information.util.model.annotation.MappedProperty;

/**
 *
 * @author Artem
 */
public class BeanPropertyUtil {

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
                } else {
                    property.setWritable(true);
                    for (Annotation annotation : prop.getReadMethod().getAnnotations()) {

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
                            excludes.add(excludeProp);
                        }


                        if(annotation.annotationType().equals(BookReference.class)){
                            property.setBeanReference(true);
                            String referencedField = ((BookReference)annotation).referencedProperty();
                            property.setReferencedField(referencedField);
                        }

                        if(annotation.annotationType().equals(JoinColumn.class)){
                            JoinColumn joinColumn = (JoinColumn)annotation;
                            property.setNullable(joinColumn.nullable());
                        }
                    }
                }
            } else {
                property.setReadable(false);
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

    public static void addLocalization(Serializable bookEntry, List<Locale> supportedLocales) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        BeanInfo beanInfo = Introspector.getBeanInfo(bookEntry.getClass());
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propDesc : props) {
            MappedProperty mappedProperty = propDesc.getReadMethod().getAnnotation(MappedProperty.class);
            if (mappedProperty != null) {
                List<StringCulture> cultures = (List<StringCulture>) propDesc.getReadMethod().invoke(bookEntry);
                if (cultures.size() == 0) {
                    for (Locale locale : supportedLocales) {
                        StringCultureId cultureId = new StringCultureId(locale.getLanguage());
                        StringCulture culture = new StringCulture(cultureId, "");
                        cultures.add(culture);
                    }
                    propDesc.getWriteMethod().invoke(bookEntry, cultures);
                }
            }
        }
    }

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

    public static Object getPropertyValue(Object target, String propertyName) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor prop : props) {
            if(prop.getName().equals(propertyName)){
                return prop.getReadMethod().invoke(target);
            }
        }
        throw new RuntimeException("Property '"+propertyName+"' was not found in type "+target.getClass());
    }

    public static String getPropertyAsString(Object propertyValue, Property property, Locale systemLocale) throws IntrospectionException {
        String asString = "";
        if (propertyValue != null) {
            if (Date.class.isAssignableFrom(property.getType())) {
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Session.get().getLocale());
                asString = dateFormat.format((Date) propertyValue);
            } else if (property.isLocalizable()) {
                Locale currentLocale = Session.get().getLocale();
                List<StringCulture> list = (List<StringCulture>) propertyValue;
                boolean finded = false;
                //try to find in current locale.
                for (StringCulture culture : list) {
                    if (new Locale(culture.getId().getLocale()).getLanguage().equalsIgnoreCase(currentLocale.getLanguage())) {
                        if (!Strings.isEmpty(culture.getValue())) {
                            finded = true;
                            asString = culture.getValue();
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
                            }
                        }
                    }
                }
                if (!finded) {
                    if (!list.isEmpty()) {
                        asString = list.get(0).getValue();
                    }
                }
            } else if(property.isBeanReference()) {
                Object referencedBook = propertyValue;
                String referencedField = property.getReferencedField();
                Object value = null;
                try {
                    value = BeanPropertyUtil.getPropertyValue(referencedBook, referencedField);
                } catch (Exception e) {
                    //TODO: remove it after testing.
                    throw new RuntimeException(e);
                }
                asString = getPropertyAsString(value, getPropertyByName(referencedBook.getClass(), referencedField), systemLocale);

            } else {
                asString = propertyValue.toString();
            }
        }
        return asString;
    }

    public static Property getPropertyByName(Class beanClass, String propertyName) throws IntrospectionException{
        for(Property prop : getProperties(beanClass)){
            if(prop.getName().equals(propertyName)){
                return prop;
            }
        }
         throw new RuntimeException("Property '"+propertyName+"' was not found in type "+beanClass);
    }

    public static void setPropertyValue(Object target, String propertyName, Object value) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor prop : props) {
            if(prop.getName().equals(propertyName)){
                prop.getWriteMethod().invoke(target, value);
            }
        }
        throw new RuntimeException("Property '"+propertyName+"' was not found in type "+target.getClass());
    }


}
