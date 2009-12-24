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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Id;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.model.StringCultureId;
import org.vetcontrol.information.util.model.annotation.MappedProperty;

/**
 *
 * @author Artem
 */
public class BeanPropertyUtil {

    public static List<Property> filter(Class<?> beanClass) throws IntrospectionException {
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
}
