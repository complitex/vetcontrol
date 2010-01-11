package org.vetcontrol.web.pages;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:57:26
 */
@AuthorizeInstantiation(SecurityRoles.AUTHORIZED)
public class HomePage extends FormTemplatePage {
    public HomePage() {
        super();
    }
}
