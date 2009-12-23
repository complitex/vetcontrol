package org.vetcontrol.user.web.pages;

import org.apache.wicket.Page;
import org.vetcontrol.user.UserHomePage;
import org.vetcontrol.web.template.ITemplateMenu;
import org.vetcontrol.web.template.ITemplateLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:16:39
 */
public class UserTemplateMenu implements ITemplateMenu{

    @Override
    public String getTitle(Locale locale) {
        return "Пользователи";
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink(){

            @Override
            public String getLabel(Locale locale) {
                return "Список";
            }

            @Override
            public Class<? extends Page> getPage() {
                return UserHomePage.class;
            }
        });

        return links;
    }
}
