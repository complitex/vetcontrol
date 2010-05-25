package org.vetcontrol.user.web.pages;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(UserTemplateMenu.class);

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

        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(UserTemplateMenu.class, locale, "user.template.menu.client.list");
            }
            @Override
            public Class<? extends Page> getPage() {
                return ClientList.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "ClientList";
            }
        });

        try {
            final Class logList = Class.forName("org.vetcontrol.logging.web.pages.LogList");

            links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(UserTemplateMenu.class, locale, "user.template.menu.log");
            }

            @SuppressWarnings({"unchecked"})
            @Override
            public Class<? extends Page> getPage() {

                return logList;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "Log";
            }

        });
        } catch (ClassNotFoundException e) {
            log.error("Модуль журнала событий не найден", e);
        }

        try {
            final Class updateList = Class.forName("org.vetcontrol.sync.server.web.pages.UpdateList");

            links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(UserTemplateMenu.class, locale, "user.template.menu.update");
            }

            @SuppressWarnings({"unchecked"})
            @Override
            public Class<? extends Page> getPage() {

                return updateList;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "Update";
            }

        });
        } catch (ClassNotFoundException e) {
            log.error("Модуль обновления клиента не найден", e);
        }


        return links;
    }

    @Override
    public String getTagId() {
        return "user_menu";
    }
}
