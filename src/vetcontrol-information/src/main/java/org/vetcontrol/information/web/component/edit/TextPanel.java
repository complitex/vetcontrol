/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.web.util.Constants;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public class TextPanel extends Panel {

    public TextPanel(String id, IModel model, Property prop, boolean enabled) {
        super(id);

        IModel labelModel = new DisplayPropertyLocalizableModel(prop);
        Class propertyType = determineFieldType(prop);

        TextField textField = new TextField("textField", model);
        textField.setType(propertyType);
        textField.setEnabled(enabled);
        textField.setLabel(labelModel);
        textField.setRequired(!prop.isNullable());

        if (prop.getLength() > 0) {
            textField.add(new SimpleAttributeModifier("maxlength", String.valueOf(prop.getLength())));
            textField.add(new MaximumLengthValidator(prop.getLength()));
        }

        TextArea textArea = new TextArea("textArea", model);
        textArea.setType(propertyType);
        textArea.setEnabled(enabled);
        textArea.setLabel(labelModel);
        textArea.setRequired(!prop.isNullable());

        if (prop.getLength() > 0) {
            textArea.add(new MaximumLengthValidator(prop.getLength()));
        }

        if (prop.getLength() > 0) {
            if (prop.getLength() <= Constants.TEXT_FIELD_MAX_LENGTH) {
                textArea.setVisible(false);
            } else {
                textField.setVisible(false);
            }
        } else {
            textField.setVisible(false);
        }
        add(textField);
        add(textArea);
    }

    protected Class determineFieldType(Property prop) {
        Class propertyType = prop.getType();
        if (prop.isLocalizable()) {
            propertyType = String.class;
        }
        return propertyType;
    }
}
