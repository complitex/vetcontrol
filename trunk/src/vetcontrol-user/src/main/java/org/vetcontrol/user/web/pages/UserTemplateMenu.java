package org.vetcontrol.user.web.pages;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ResourceTemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:16:39
 *
 * Реализация меню модуля Редактирование Пользователей для общего шаблона сайта
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UserTemplateMenu extends ResourceTemplateMenu{

    @Override
    public String getTitle(Locale locale) {
        return getString(UserTemplateMenu.class, locale, "user.template.menu.title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(UserTemplateMenu.class, locale, "user.template.menu.list");                
            }
            @Override
            public Class<? extends Page> getPage() {
                return UserList.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "UserList";
            }

        });

        return links;
    }

    @Override
    public String getTagId() {
        return "user_menu";
    }
}
