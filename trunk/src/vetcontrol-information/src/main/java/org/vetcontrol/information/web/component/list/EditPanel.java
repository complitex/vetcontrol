package org.vetcontrol.information.web.component.list;

import java.io.Serializable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.web.util.CanEditUtil;

public abstract class EditPanel extends Panel {

    public EditPanel(String id, final IModel<? extends Serializable> model) {
        super(id, model);
        Link editLink = new Link("editLink") {

            @Override
            public void onClick() {
                selected(model.getObject());
            }
        };
        WebMarkupContainer edit = new WebMarkupContainer("edit");
        edit.setVisible(CanEditUtil.canEdit(model.getObject()));
        editLink.add(edit);
        WebMarkupContainer view = new WebMarkupContainer("view");
        view.setVisible(!CanEditUtil.canEdit(model.getObject()));
        editLink.add(view);
        add(editLink);
    }

    protected abstract void selected(Serializable bean);
}
