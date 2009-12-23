/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.util.web.Property;

/**
 *
 * @author Artem
 */
public final class TextPanel extends Panel {

    public TextPanel(String id, IModel model, Property prop) {
        super(id);

        TextField textField = new TextField("textField");
        textField.setModel(model);
        textField.setRequired(!prop.isNullable());

        if (prop.getLength() > 0) {
            textField.add(new SimpleAttributeModifier("maxlength", String.valueOf(prop.getLength())));
        }

        add(textField);
    }
}
