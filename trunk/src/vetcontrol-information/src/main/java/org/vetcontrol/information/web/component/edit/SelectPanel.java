/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.web.component.list.BookChoiceRenderer;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public final class SelectPanel extends Panel {

    public SelectPanel(String id, IModel model, final Property property, List books, final Locale systemLocale) {
        super(id);

        BookChoiceRenderer choiceRenderer = new BookChoiceRenderer(property, systemLocale);
        DropDownChoice dropDownChoice = new DropDownChoice("select", model, books, choiceRenderer);
        dropDownChoice.setLabel(new DisplayPropertyLocalizableModel(property, this));
        if (property.isNullable()) {
            dropDownChoice.setNullValid(true);
        }

        dropDownChoice.setRequired(!property.isNullable());

        add(dropDownChoice);
    }
}
