package org.vetcontrol.information.web.component.list;

import java.io.Serializable;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.web.security.SecurityRoles;

public class EditPanel extends Panel {

    public EditPanel(String id, final IModel<? extends Serializable> model, final ISelectable selectable) {
        super(id, model);
        Link editLink = new Link("edit") {

            @Override
            public void onClick() {
                selectable.selected(model.getObject());
            }
        };
        MetaDataRoleAuthorizationStrategy.authorize(editLink, RENDER, SecurityRoles.INFORMATION_EDIT);
        add(editLink);
    }
}
