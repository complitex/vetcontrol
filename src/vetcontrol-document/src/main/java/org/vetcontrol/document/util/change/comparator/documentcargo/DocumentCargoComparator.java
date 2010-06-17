/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator.documentcargo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.vetcontrol.document.web.pages.DocumentCargoEdit;
import org.vetcontrol.document.util.change.comparator.DocumentComparator;
import org.vetcontrol.document.util.change.comparator.PassingBorderPointComparator;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.util.change.comparator.SimpleComparator;
import org.vetcontrol.util.change.comparator.LocalizableBookReferenceComparator;
import org.vetcontrol.web.component.MovementTypeChoicePanel;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;

/**
 *
 * @author Artem
 */
public class DocumentCargoComparator extends DocumentComparator<DocumentCargo> {

    private static final String RESOURCES = DocumentCargoEdit.class.getName();

    public DocumentCargoComparator(Locale systemLocale) {
        super(DocumentCargo.class, systemLocale, RESOURCES);
    }

    @Override
    protected Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = new HashMap<String, Comparator>();

        propertyComparators.put("movementType", new SimpleComparator<MovementType>(getString("document.cargo.movement_type")) {

            @Override
            protected String displayPropertyValue(MovementType value) {
                return MovementTypeChoicePanel.getDysplayName(value, getSystemLocale());
            }
        });
        propertyComparators.put("vehicleType", new SimpleComparator<VehicleType>(getString("document.cargo.vehicle_type")) {

            @Override
            protected String displayPropertyValue(VehicleType value) {
                return VehicleTypeChoicePanel.getDysplayName(value, getSystemLocale());
            }
        });
        propertyComparators.put("senderCountry", new LocalizableBookReferenceComparator(getString("document.cargo.cargo_sender_country"),
                getSystemLocale()));
        propertyComparators.put("senderName", new SimpleComparator<String>(getString("document.cargo.cargo_sender_name")));
        propertyComparators.put("receiverName", new SimpleComparator<String>(getString("document.cargo.cargo_receiver_name")));
        propertyComparators.put("receiverAddress", new SimpleComparator<String>(getString("document.cargo.cargo_receiver_address")));
        propertyComparators.put("details", new SimpleComparator(getString("document.cargo.details")));
        propertyComparators.put("passingBorderPoint", new PassingBorderPointComparator(getString("document.cargo.passingBorderPoint")));
        propertyComparators.put("department", new LocalizableBookReferenceComparator(getString("document.cargo.department"), getSystemLocale()));
        propertyComparators.put("cargos", new CargoListComparator(getString("document.cargo.cargo_list"), getSystemLocale()));

        return propertyComparators;
    }
}
