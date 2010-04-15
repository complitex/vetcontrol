/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.report.entity.CargosInDayReport;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed(SecurityRoles.LOCAL_AND_REGIONAL_REPORT)
public class CargosInDayReportDAO extends AbstractReportDAO<CargosInDayReport> {

    public static enum OrderBy {

        CARGO_TYPE("cargoTypeName"), CARGO_SENDER("cargoSenderName"), CARGO_RECEIVER("cargoReceiverName"), CARGO_PRODUCER("cargoProducerName");
        private String name;

        private OrderBy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public CargosInDayReportDAO() {
        super(CargosInDayReport.class);
    }

    @Override
    protected String prepareAllSQL(String sqlPattern, Map<String, Object> parameters, Locale reportLocale, String sortProperty, boolean isAscending) {
        return MessageFormat.format(sqlPattern, sortProperty, isAscending ? "ASC" : "DESC");
    }
}
