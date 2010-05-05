/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.entity;

import org.vetcontrol.report.commons.entity.ReportParameter;

/**
 *
 * @author Artem
 */
public interface CargosInDayReportParameter extends ReportParameter {

    String START_DATE = "startDate";
    String END_DATE = "endDate";

    String DAY = "day";
}
