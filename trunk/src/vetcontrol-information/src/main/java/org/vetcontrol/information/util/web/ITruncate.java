/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.util.web;

import java.io.Serializable;
import org.vetcontrol.util.book.Property;

/**
 *
 * @author Artem
 */
public interface ITruncate extends Serializable {

    String truncate(String fullValue, Property property);

}
