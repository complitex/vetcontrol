/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.io.Serializable;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.vetcontrol.information.web.util.CanEditUtil;
import org.vetcontrol.information.web.util.PageManager;

/**
 *
 * @author Artem
 */
public class GoToListPagePanel extends Panel {

    public GoToListPagePanel(String id, final Serializable book) {
        super(id);

        Link<Void> cancel = new Link<Void>("cancel") {

            @Override
            public void onClick() {
                goToListPage(book.getClass());
            }
        };
        cancel.setVisible(CanEditUtil.canEdit(book));
        add(cancel);

        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                goToListPage(book.getClass());
            }
        };
        back.setVisible(!CanEditUtil.canEdit(book));
        add(back);
    }

    protected void goToListPage(final Class<?> entityClass) {
        PageManager.goToListPage(this, entityClass);
    }
}
