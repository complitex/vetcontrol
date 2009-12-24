/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.service.dao;

import java.util.List;
import javax.ejb.Local;
import org.vetcontrol.entity.Locale;

/**
 *
 * @author Artem
 */
@Local
public interface ILocaleDAO {

    List<Locale> all();

    Locale systemLocale();

}
