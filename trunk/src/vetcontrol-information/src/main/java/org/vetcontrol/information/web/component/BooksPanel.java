/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.io.Serializable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.odlabs.wiquery.core.events.Event;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.odlabs.wiquery.core.events.WiQueryEventBehavior;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsScope;
import org.vetcontrol.information.web.support.BookTypes;

/**
 *
 * @author Artem
 */
@Deprecated
public final class BooksPanel extends Panel {

    public static final String BOOK_TYPE_PARAM = "bookType";

    public BooksPanel(String id) {
        super(id);
        init();
    }

    private void init() {

       WebMarkupContainer expand = new WebMarkupContainer("Expand");
       final WebMarkupContainer content = new WebMarkupContainer("Content");

       expand.add(new WiQueryEventBehavior(new Event(MouseEvent.CLICK) {

            @Override
            public JsScope callback() {
                return JsScope.quickScope(new JsQuery(content).$().chain("toggle", "'slow'").render());
            }
        }));
        add(expand);


        add(content);
        content.add(new ListView<Class>("Books", BookTypes.getList()) {

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

