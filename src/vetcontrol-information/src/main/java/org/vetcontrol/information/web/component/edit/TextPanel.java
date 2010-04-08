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
import org.apache.wicket.validation.validator.StringValidator;
import org.vetcontrol.information.util.web.Constants;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public final class TextPanel extends Panel {

    public TextPanel(String id, IModel model, Property prop, boolean enabled) {
        super(id);

        IModel labelModel = new DisplayPropertyLocalizableModel(prop, this);

        TextField textField = new TextField("textField", model);
        textField.setEnabled(enabled);
        textField.setLabel(labelModel);
        textField.setRequired(!prop.isNullable());

        if (prop.getLength() > 0) {
            textField.add(new SimpleAttributeModifier("maxlength", String.valueOf(prop.getLength())));
        }

        TextArea textArea = new TextArea("textArea", model);
        textArea.setEnabled(enabled);
        textArea.setLabel(labelModel);
        textArea.setRequired(!prop.isNullable());

        if (prop.getLength() > 0) {
            textArea.add(StringValidator.maximumLength(prop.getLength()));
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
}
