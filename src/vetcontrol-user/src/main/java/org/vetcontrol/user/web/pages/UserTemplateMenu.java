package org.vetcontrol.user.web.pages;

import org.apache.wicket.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ITemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.web.security.SecurityRoles;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:16:39
 *
 * Реализация меню модуля Редактирование Пользователей для общего шаблона сайта
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UserTemplateMenu implements ITemplateMenu{
    private static final Logger log = LoggerFactory.getLogger(UserTemplateMenu.class);

    @Override
    public String getTitle(Locale locale) {
        try {
            return getResourceBundle(locale).getString("user.template.menu.title");
        } catch (Exception e) {
            log.error("Не найдено значение заголовка меню в файле локализации", e);
        }
        return "[User Menu]";
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                try {
                    return getResourceBundle(locale).getString("user.template.menu.list");
                } catch (Exception e) {
                    log.error("Не найдено значение названия ссылки меню в файле локализации", e);
                }
                return "[LIST]";
            }
            @Override
            public Class<? extends Page> getPage() {
                return UserList.class;
            }
        });

        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                try {
                    return getResourceBundle(locale).getString("user.template.menu.add");
                } catch (Exception e) {
                    log.error("Не найдено значение названия ссылки меню в файле локализации", e);
                }
                return "[ADD]";
            }
            @Override
            public Class<? extends Page> getPage() {
                return UserEdit.class;
            }
        });

        return links;
    }

    /**
     * Используется ResourceBundle для локализации меню
     * @param locale текущая локаль
     * @return ResourceBundle
     */
    private ResourceBundle getResourceBundle(Locale locale){
        try {
            return ResourceBundle.getBundle("org.vetcontrol.user.web.pages.UserTemplateMenu", locale);
        } catch (Exception e) {
            log.error("Ресурс файла локализации не найден", e);
        }
        return null;
    }
}
