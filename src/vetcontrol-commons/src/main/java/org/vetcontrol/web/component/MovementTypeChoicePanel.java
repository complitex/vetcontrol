/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.vetcontrol.entity.MovementType;

/**
 *
 * @author Artem
 */
public final class MovementTypeChoicePanel extends Panel {

    public MovementTypeChoicePanel(String id, IModel<MovementType> model, MovementType[] choices, boolean required, String labelId) {
        super(id);
        init(model, choices, required, labelId);
    }

    private void init(IModel<MovementType> model, MovementType[] choices, boolean required, final String labelId) {
        DropDownChoice<MovementType> select = new DropDownChoice<MovementType>("select", model,
                choices != null ? Arrays.asList(choices) : Arrays.asList(MovementType.values()),
                new EnumChoiceRenderer<MovementType>(this));
        select.setRequired(required);
        if (!Strings.isEmpty(labelId)) {
            select.setLabel(new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {
                    return getPage().getString(labelId);
                }
            });
        }
        add(select);
    }

    public static final String getDysplayName(MovementType movementType, Locale locale) {
        return ResourceBundle.getBundle(MovementTypeChoicePanel.class.getName(), locale).
                getString(MovementType.class.getSimpleName() + "." + movementType.name());
    }
}
