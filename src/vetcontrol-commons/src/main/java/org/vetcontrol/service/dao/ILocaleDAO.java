/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao;

import java.util.List;
import java.util.Locale;
import javax.ejb.Local;

/**
 *
 * @author Artem
 */
@Local
public interface ILocaleDAO {

    List<Locale> all();

    Locale systemLocale();

}
