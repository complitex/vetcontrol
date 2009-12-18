/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.web;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author Artem
 */
public class BeanPropertiesFilter {

    public static List<Property> filter(Class<?> beanClass) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        List<Property> filtered = new ArrayList<Property>();
        for (PropertyDescriptor prop : props) {

            boolean validProperty = true;

            if (prop.getName().equals("class")) {
                validProperty = false;
            }

            Property property = new Property();
            property.setName(prop.getDisplayName());
            property.setType(prop.getPropertyType());

            if (prop.getReadMethod() != null &&
                    Modifier.isPublic(prop.getReadMethod().getModifiers()) &&
                    !Modifier.isAbstract(prop.getReadMethod().getModifiers())) {

                property.setReadable(true);

                if (prop.getWriteMethod() == null ||
                        !Modifier.isPublic(prop.getWriteMethod().getModifiers()) ||
                        Modifier.isAbstract(prop.getWriteMethod().getModifiers())) {

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
                    }
                }
            } else {
                property.setReadable(false);
            }

            if (validProperty) {
                filtered.add(property);
            }
        }

        return filtered;
    }
}
