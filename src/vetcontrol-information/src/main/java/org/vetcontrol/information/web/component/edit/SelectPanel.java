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
import org.vetcontrol.information.util.web.TruncateUtil;
import org.vetcontrol.information.web.component.BookChoiceRenderer;
import org.vetcontrol.information.web.component.DisableAwareDropDownChoice;
import org.vetcontrol.book.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public final class SelectPanel extends Panel {

    public SelectPanel(String id, IModel model, final Property property, List books, final Locale systemLocale, boolean enabled) {
        super(id);

        BookChoiceRenderer choiceRenderer = new BookChoiceRenderer(property, systemLocale, TruncateUtil.TRUNCATE_SELECT_VALUE_IN_EDIT_PAGE);
        DropDownChoice dropDownChoice = new DisableAwareDropDownChoice("select", model, books, choiceRenderer);
        dropDownChoice.setEnabled(enabled);
        dropDownChoice.setLabel(new DisplayPropertyLocalizableModel(property, this));
        if (property.isNullable()) {
            dropDownChoice.setNullValid(true);
        }

        dropDownChoice.setRequired(!property.isNullable());

        add(dropDownChoice);
    }
}
