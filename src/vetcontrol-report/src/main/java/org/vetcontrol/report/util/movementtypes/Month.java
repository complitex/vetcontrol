/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.util.movementtypes;

import java.util.Locale;
import org.vetcontrol.util.DateUtil;

/**
 *
 * @author Artem
 */
public enum Month {

    JAN(0), FEB(1), MAR(2), APR(3), MAY(4), JUN(5), JUL(6), AUG(7), SEP(8), OCT(9), NOV(10), DEC(11);

    private Month(int number) {
        this.number = number;
    }

    private int number;

    public String getDisplayName(Locale locale){
        return DateUtil.getDisplayMonth(number, locale);
    }

    public int getNumber() {
        return number;
    }
}
