/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.util.change.comparator.arrest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.vetcontrol.document.util.change.comparator.DocumentComparator;
import org.vetcontrol.document.web.pages.ArrestDocumentEdit;
import org.vetcontrol.entity.ArrestDocument;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.util.change.comparator.Comparator;
import org.vetcontrol.util.change.comparator.DateComparator;
import org.vetcontrol.util.change.comparator.LocalizableBookReferenceComparator;
import org.vetcontrol.util.change.comparator.PassingBorderPointComparator;
import org.vetcontrol.util.change.comparator.SimpleComparator;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;

/**
 *
 * @author Artem
 */
public class ArrestDocumentComparator extends DocumentComparator<ArrestDocument> {

    public ArrestDocumentComparator(Locale systemLocale) {
        super(ArrestDocument.class, systemLocale);
    }

    private static final String RESOURCE_BUNDLE = ArrestDocumentEdit.class.getName();

    @Override
    protected String getResourceBundle() {
        return RESOURCE_BUNDLE;
    }

    @Override
    protected Map<String, Comparator> getPropertyComparators() {
        Map<String, Comparator> propertyComparators = new HashMap<String, Comparator>();
        propertyComparators.put("arrestDate", new DateComparator(getString("arrest.document.arrest_date"), getSystemLocale()));
        propertyComparators.put("arrestReason", new LocalizableBookReferenceComparator(getString("arrest.document.arrest_reason"), getSystemLocale()));
        propertyComparators.put("arrestReasonDetails", new SimpleComparator<String>(getString("arrest.document.arrest_reason_details")));
        propertyComparators.put("cargoType", new LocalizableBookReferenceComparator(getString("arrest.document.cargo_type"), getSystemLocale()));
        propertyComparators.put("cargoMode", new LocalizableBookReferenceComparator(getString("arrest.document.cargo_mode"), getSystemLocale()));
        propertyComparators.put("unitType", new LocalizableBookReferenceComparator(getString("arrest.document.unit_type"), getSystemLocale()));
        propertyComparators.put("count", new SimpleComparator<Double>(getString("arrest.document.count")));
        propertyComparators.put("vehicleType", new SimpleComparator<VehicleType>(getString("arrest.document.vehicle_type")) {

            @Override
            protected String displayPropertyValue(VehicleType value) {
                return VehicleTypeChoicePanel.getDisplayName(value, getSystemLocale());
            }
        });
        propertyComparators.put("vehicleDetails", new SimpleComparator<String>(getString("arrest.document.vehicle_details")));
        propertyComparators.put("senderCountry", new LocalizableBookReferenceComparator(getString("arrest.document.cargo_sender_country"),
                getSystemLocale()));
        propertyComparators.put("senderName", new SimpleComparator<String>(getString("arrest.document.cargo_sender_name")));
        propertyComparators.put("receiverName", new SimpleComparator<String>(getString("arrest.document.cargo_receiver_name")));
        propertyComparators.put("receiverAddress", new SimpleComparator<String>(getString("arrest.document.cargo_receiver_address")));
        propertyComparators.put("documentCargoCreated", new DateComparator(getString("arrest.document.document_cargo_created"), getSystemLocale()));
        propertyComparators.put("certificateDetails", new SimpleComparator<String>(getString("arrest.document.certificate_detail")));
        propertyComparators.put("certificateDate", new DateComparator(getString("arrest.document.certificate_date"), getSystemLocale()));
        propertyComparators.put("department", new LocalizableBookReferenceComparator(getString("arrest.document.department"), getSystemLocale()));
        propertyComparators.put("passingBorderPoint", new PassingBorderPointComparator(getString("arrest.document.passingBorderPoint")));

        return propertyComparators;
    }
}
