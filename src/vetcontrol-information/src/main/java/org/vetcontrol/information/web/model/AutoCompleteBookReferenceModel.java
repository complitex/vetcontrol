/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.Property;

/**
 *
 * @author Artem
 */
public class AutoCompleteBookReferenceModel implements IModel<String> {

    private Property property;
    private IModel model;

    public AutoCompleteBookReferenceModel(Property property, IModel model) {
        this.property = property;
        this.model = model;
    }

    private IModel<String> newReferencedBookModel(Property property, IModel model) {
        if (model.getObject() == null) {
            Object referencedBook = null;
            try {
                referencedBook = property.getType().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            model.setObject(referencedBook);
        }

        Property referencedProperty = BeanPropertyUtil.getPropertyByName(property.getType(), property.getReferencedField());
        IModel<String> referencedBookModel = new PropertyModel(model.getObject(), referencedProperty.getName());
        if (referencedProperty.isLocalizable()) {
            referencedBookModel = new StringCultureModel(referencedBookModel);
        }
        return referencedBookModel;
    }

    @Override
    public String getObject() {
        IModel<String> referencedBookModel = newReferencedBookModel(property, model);
        return referencedBookModel.getObject();
    }

    @Override
    public void setObject(String object) {
        IModel<String> referencedBookModel = newReferencedBookModel(property, model);
        referencedBookModel.setObject(object);
    }

    @Override
    public void detach() {
    }
}
