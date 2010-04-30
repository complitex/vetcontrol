/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.entity.ExtendedArrestReport;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public class ArrestReportDAO extends AbstractReportDAO<ExtendedArrestReport> {

    public static enum OrderBy {

        DEPARTMENT("departmentName"),
        PASSING_BORDER_POINT("passingBorderPointName"),
        ARREST_DATE("arrestDate"),
        CARGO_TYPE("cargoTypeName"),
        VEHICLE_TYPE("vehicleType");
        private String name;

        private OrderBy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public ArrestReportDAO() {
        super(ExtendedArrestReport.class);
    }

    @Override
    protected String prepareAllSQL(String sqlPattern, Map<String, Object> parameters, Locale reportLocale, String sortProperty, Boolean isAscending) {
        String realSortProperty = sortProperty;
        if ("vehicleType".equals(sortProperty)) {
            realSortProperty = orderByVehicleType(reportLocale);
        }
        return MessageFormat.format(sqlPattern, realSortProperty, isAscending ? "ASC" : "DESC");
    }

    private static String orderByVehicleType(Locale locale) {
        StringBuilder orderByBuilder = new StringBuilder();
        orderByBuilder.append(" (CASE vehicleType ");
        for (VehicleType vehicleType : VehicleType.values()) {
            orderByBuilder.append("WHEN '").
                    append(vehicleType.name()).
                    append("' THEN '").
                    append(VehicleTypeChoicePanel.getDysplayName(vehicleType, locale)).
                    append("' ");
        }
        orderByBuilder.append("END)");
        return orderByBuilder.toString();
    }
}
