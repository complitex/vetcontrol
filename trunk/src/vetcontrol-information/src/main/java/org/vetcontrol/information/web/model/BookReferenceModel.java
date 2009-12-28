/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.model.StringCultureId;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Property;

/**
 *
 * @author Artem
 */
public class BookReferenceModel implements IModel<Serializable> {

    private IModel model;
    private String property;
    private Class bookType;

    public BookReferenceModel(Class referencedBookClass, IModel model, String propertyName) {
        this.model = model;
        this.property = propertyName;
        this.bookType = referencedBookClass;
    }

    @Override
    public Serializable getObject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(Serializable object) {
        String value = (String) object;
        try {
            Object book = bookType.newInstance();
            Property prop = BeanPropertyUtil.getPropertyByName(bookType, property);
            if (!prop.isLocalizable()) {
                if (!prop.isBeanReference()) {
                    BeanPropertyUtil.setPropertyValue(book, property, value);
                    model.setObject(book);
                }
            } else {
                BeanPropertyUtil.setPropertyValue(book, property, new StringCulture(new StringCultureId(), value));
            }

            model.setObject(object);
        } catch (Exception e) {
            //TODO: remove it after testing.
            throw new RuntimeException(e);
        }

    }

    @Override
    public void detach() {
    }
}
