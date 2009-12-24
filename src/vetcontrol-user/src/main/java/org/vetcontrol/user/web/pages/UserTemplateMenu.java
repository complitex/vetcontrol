package org.vetcontrol.user.web.pages;

import org.apache.wicket.Page;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ITemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:16:39
 */
public class UserTemplateMenu implements ITemplateMenu{

    @Override
    public String getTitle(Locale locale) {
        return getResourceBundle(locale).getString("user.template.menu.title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getResourceBundle(locale).getString("user.template.menu.list");
            }
            @Override
            public Class<? extends Page> getPage() {
                return UserList.class;
            }
        });

        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getResourceBundle(locale).getString("user.template.menu.add");
            }
            @Override
            public Class<? extends Page> getPage() {
                return UserEdit.class;
            }
        });                               

        return links;
    }

    private ResourceBundle getResourceBundle(Locale locale){        
        return ResourceBundle.getBundle("org.vetcontrol.user.web.pages.UserTemplateMenu", locale);
    }
}
