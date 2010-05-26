/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util;

import org.apache.wicket.Component;

/**
 *
 * @author Artem
 */
public final class PageManager {

    private PageManager() {
    }

    public static void goToListPage(Component component, Class bookType) {
        component.setResponsePage(BookWebInfoContainer.getListPage(bookType), BookWebInfoContainer.getListPageParameters(bookType));
    }

    public static void goToEditPage(Component component, Class bookType) {
        component.setResponsePage(BookWebInfoContainer.getEditPage(bookType));
    }
}
