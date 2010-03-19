package org.vetcontrol.web.pages.welcome;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:57:26
 */
@AuthorizeInstantiation(SecurityRoles.AUTHORIZED)
public class WelcomePage extends FormTemplatePage {
    public WelcomePage() {
        super();

        add(new Label("title", new ResourceModel("title")));
    }
}
