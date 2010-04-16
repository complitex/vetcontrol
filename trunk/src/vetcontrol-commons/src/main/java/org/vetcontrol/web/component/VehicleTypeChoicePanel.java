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
import org.apache.wicket.model.IModel;
import org.vetcontrol.entity.VehicleType;

/**
 *
 * @author Artem
 */
public final class VehicleTypeChoicePanel extends Panel {

    public VehicleTypeChoicePanel(String id, IModel<VehicleType> model, boolean required) {
        super(id);
        init(model, required);
    }

    private void init(IModel<VehicleType> model, boolean required) {
        DropDownChoice<VehicleType> select = new DropDownChoice<VehicleType>("select", model, Arrays.asList(VehicleType.values()),
                new EnumChoiceRenderer<VehicleType>(this));
        select.setRequired(required);
        add(select);
    }

    public static final String getDysplayName(VehicleType vehicleType, Locale locale) {
        return ResourceBundle.getBundle(VehicleTypeChoicePanel.class.getName(), locale).
                getString(VehicleType.class.getSimpleName() + "." + vehicleType.name());
    }
}
