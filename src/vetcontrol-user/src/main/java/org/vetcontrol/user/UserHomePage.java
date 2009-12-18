package org.vetcontrol.user;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 21:32:14
 */
@AuthorizeInstantiation("USER")
public class UserHomePage extends WebPage{
}
