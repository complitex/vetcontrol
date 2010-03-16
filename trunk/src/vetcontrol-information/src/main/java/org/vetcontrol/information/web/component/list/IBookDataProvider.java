/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.list;

/**
 *
 * @author Artem
 */
public interface IBookDataProvider {

    void initSize();

    void init(Class type, String sortProperty, boolean isAscending);
}
