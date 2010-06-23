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
import org.vetcontrol.entity.VehicleType;

/**
 *
 * @author Artem
 */
public final class VehicleTypeChoicePanel extends Panel {

    public VehicleTypeChoicePanel(String id, IModel<VehicleType> model, boolean required, String labelId) {
        super(id);
        init(model, required, labelId);
    }

    private void init(IModel<VehicleType> model, boolean required, final String labelId) {
        DropDownChoice<VehicleType> select = new DropDownChoice<VehicleType>("select", model, Arrays.asList(VehicleType.values()),
                new EnumChoiceRenderer<VehicleType>(this));
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

    public DropDownChoice<VehicleType> getDropDownComponent() {
        return (DropDownChoice<VehicleType>) get("select");
    }

    public static final String getDisplayName(VehicleType vehicleType, Locale locale) {
        return ResourceBundle.getBundle(VehicleTypeChoicePanel.class.getName(), locale).
                getString(VehicleType.class.getSimpleName() + "." + vehicleType.name());
    }
}
