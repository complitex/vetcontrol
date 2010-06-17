/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.change.comparator.custom.cargomode;

import java.util.Locale;
import java.util.Map;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeEdit;
import org.vetcontrol.information.util.resource.ResourceUtil;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.information.util.change.comparator.BookComparator;

/**
 *
 * @author Artem
 */
public class CargoModeComparator extends BookComparator<CargoMode> {

    private static final String CMCT_PROP = "cargoModeCargoTypes";

    private static final String CMUT_PROP = "cargoModeUnitTypes";

    public CargoModeComparator(Locale systemLocale) {
        super(CargoMode.class, systemLocale);
    }

    @Override
    public Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = super.getPropertyComparators();

        String localizedCmctProp = ResourceUtil.getString(CargoModeEdit.class.getName(), "cargoMode.edit.cargoTypes", getSystemLocale(), true);
        propertyComparators.put(CMCT_PROP, new CargoTypeListComparator(localizedCmctProp));

        String localizedCmutProp = ResourceUtil.getString(CargoModeEdit.class.getName(), "cargoMode.edit.unitTypes", getSystemLocale(), true);
        propertyComparators.put(CMUT_PROP, new UnitTypeListComparator(localizedCmutProp, getSystemLocale()));
        return propertyComparators;
    }
}
