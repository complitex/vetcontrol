/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.util.change.comparator;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.vetcontrol.entity.Change;

/**
 *
 * @author Artem
 */
public abstract class AbstractEntityComparator<T> implements Comparator<T> {

    private Locale systemLocale;

    private Class entityClass;

    public AbstractEntityComparator(Class entityClass, Locale systemLocale) {
        this.systemLocale = systemLocale;
        this.entityClass = entityClass;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public Locale getSystemLocale() {
        return systemLocale;
    }

    @Override
    public Set<Change> compare(T oldValue, T newValue) {
        Class beanClass = oldValue.getClass();
        Map<String, Comparator> propertyComparators = getPropertyComparators();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();

            Set<Change> changes = new HashSet<Change>();

            for (PropertyDescriptor propDesc : props) {
                String propName = propDesc.getName();
                Comparator propertyComparator = propertyComparators.get(propName);
                if (propertyComparator != null) {
                    Object oldPropertyValue = propDesc.getReadMethod().invoke(oldValue);
                    Object newPropertyValue = propDesc.getReadMethod().invoke(newValue);

                    Set<Change> propertyChanges = propertyComparator.compare(oldPropertyValue, newPropertyValue);
                    changes.addAll(propertyChanges);
                }
            }
            return changes;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Map<String, Comparator> getPropertyComparators();
}
