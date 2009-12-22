/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.server.web.component;

import java.lang.reflect.Constructor;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Artem
 */
public final class AvailableBooksPanel extends Panel {

    private static final String BOOK_PANEL_CLASS_NAME = "org.vetcontrol.information.web.component.BooksPanel";

    public AvailableBooksPanel(String id) {
        super(id);
        
        Panel bookPanel = getBookPanel();
        if (bookPanel != null) {

            //TODO: add right to view books' list.
            boolean isAllow = true;

            if (!isAllow) {
                bookPanel.setVisible(false);
            }
        }
        add(bookPanel);
    }

    private Panel getBookPanel() {
        try {
            Class c = Thread.currentThread().getContextClassLoader().loadClass(BOOK_PANEL_CLASS_NAME);
            Constructor constr = c.getConstructor(String.class);
            Object o = constr.newInstance("BooksPanel");
            return (Panel)o;
        } catch (Exception e) {
        }
        return new EmptyPanel("BooksPanel");
    }
}

