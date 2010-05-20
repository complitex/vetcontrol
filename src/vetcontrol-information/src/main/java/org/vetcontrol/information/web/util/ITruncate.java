/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.web.util;

import java.io.Serializable;
import org.vetcontrol.book.Property;

/**
 *
 * @author Artem
 */
public interface ITruncate extends Serializable {

    String truncate(String fullValue, Property property);

}
