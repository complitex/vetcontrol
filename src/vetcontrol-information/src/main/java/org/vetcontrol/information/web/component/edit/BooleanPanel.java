/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.web.component.edit;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.book.Property;

/**
 *
 * @author Artem
 */
public final class BooleanPanel extends Panel {
    public BooleanPanel(String id, IModel model, Property prop, boolean enabled) {
        super (id);

        IModel labelModel = new DisplayPropertyLocalizableModel(prop, this);

        CheckBox checkBox = new CheckBox("checkbox", model);
        checkBox.setLabel(labelModel).setEnabled(enabled);
        add(checkBox);
    }
}
