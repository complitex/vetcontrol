/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.util.Locale;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.report.entity.MovementTypesReport;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed({SecurityRoles.LOCAL_AND_REGIONAL_REPORT})
public class MovementTypesReportDAO extends AbstractReportDAO<MovementTypesReport> {

    public MovementTypesReportDAO() {
        super(MovementTypesReport.class);
    }


    @Override
    protected String prepareAllSQL(String sqlPattern, Map<String, Object> parameters, Locale reportLocale, String sortProperty, boolean isAscending) {
        return sqlPattern + (isAscending ? " ASC" : " DESC");
    }
}
