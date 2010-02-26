/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.vetcontrol.report.service.LocaleService;

/**
 *
 * @author Artem
 */
@Singleton
public class DateConverter {
    
    @EJB
    private LocaleService localeService;

    private SimpleDateFormat dateFormat;

    @PostConstruct
    private void init(){
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", localeService.getReportLocale());
    }

    public Date toDate(String date){
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public String toString(Date date){
        return dateFormat.format(date);
    }

}
