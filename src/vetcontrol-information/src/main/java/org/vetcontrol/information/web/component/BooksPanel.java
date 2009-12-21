/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.io.Serializable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.vetcontrol.information.web.support.BookTypes;

/**
 *
 * @author Artem
 */
public final class BooksPanel extends Panel {

    public static final String BOOK_TYPE_PARAM = "bookType";

    public BooksPanel(String id) {
        super(id);
        init();
    }

    private void init() {

        add(new ListView<Class>("Books", BookTypes.BOOK_TYPES) {

            @Override
            protected void populateItem(ListItem<Class> item) {
                final Class<Serializable> c = item.getModelObject();
                Link link = new Link("BookContentRef") {

                    @Override
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.add(BOOK_TYPE_PARAM, c.getName());
                        //TODO: add book content page reference.
                    }
                };
                link.add(new Label("CurrentBook", new ResourceModel(c.getSimpleName(), c.getSimpleName())));
                item.add(link);
            }
        });
    }
}

