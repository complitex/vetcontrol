/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.service.dao;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.vetcontrol.report.commons.service.dao.AbstractReportDAO;
import org.vetcontrol.report.entity.MeatInYearReport;
import org.vetcontrol.web.security.SecurityRoles;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed({SecurityRoles.REGIONAL_REPORT})
public class MeatInYearReportDAO extends AbstractReportDAO<MeatInYearReport> {

    public MeatInYearReportDAO() {
        super(MeatInYearReport.class);
    }

    @Override
    protected List<MeatInYearReport> afterLoad(List<MeatInYearReport> results) {
        boolean previousCargoModeIsRoot = false;
        for (MeatInYearReport report : results) {
            if (!report.isTotalEntry()) {
                if (report.isRootCargoMode()) {
                    previousCargoModeIsRoot = true;
                } else {
                    if (previousCargoModeIsRoot) {
                        report.setFirstSubCargoMode(true);
                        previousCargoModeIsRoot = false;
                    }
                }
            }
        }
        return results;
    }
}
