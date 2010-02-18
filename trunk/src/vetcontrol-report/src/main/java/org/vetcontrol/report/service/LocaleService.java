/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.service;

import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.vetcontrol.service.dao.ILocaleDAO;

/**
 *
 * @author Artem
 */
@Singleton
public class LocaleService {

    @EJB
    private ILocaleDAO localeDAO;

    public Locale getReportLocale(){
        return localeDAO.systemLocale();
    }

}
