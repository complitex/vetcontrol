package org.vetcontrol.information.web.component.list;

import java.io.Serializable;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.security.SecurityRoles;

public abstract class EditPanel extends Panel {

    public EditPanel(String id, final IModel<? extends Serializable> model) {
        super(id, model);
        Link editLink = new Link("edit") {

            @Override
            public void onClick() {
                selected(model.getObject());
            }
        };
        MetaDataRoleAuthorizationStrategy.authorize(editLink, RENDER, SecurityRoles.INFORMATION_EDIT);
        boolean disabled = (Boolean)BeanPropertyUtil.getPropertyValue(model.getObject(), BeanPropertyUtil.getDisabledPropertyName());
        editLink.setVisible(!disabled);
        add(editLink);
    }

    protected abstract void selected(Serializable bean);
}
