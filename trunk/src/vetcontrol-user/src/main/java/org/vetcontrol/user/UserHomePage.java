package org.vetcontrol.user;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.vetcontrol.web.template.TemplatePage;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 21:32:14
 */
@AuthorizeInstantiation("admin")
public class UserHomePage extends TemplatePage{
    public UserHomePage() {
        super();
    }
}
