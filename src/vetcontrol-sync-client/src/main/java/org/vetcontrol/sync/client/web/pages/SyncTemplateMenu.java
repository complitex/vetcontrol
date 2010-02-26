package org.vetcontrol.sync.client.web.pages;

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
 *         Date: 25.02.2010 1:50:02
 */
@AuthorizeInstantiation(SecurityRoles.AUTHORIZED)
public class SyncTemplateMenu  extends ResourceTemplateMenu {
    @Override
    public String getTitle(Locale locale) {
        return getString(SyncTemplateMenu.class, locale, "sync.client.menu.title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
       List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(SyncTemplateMenu.class, locale, "sync.client.menu.sync_page");
            }
            @Override
            public Class<? extends Page> getPage() {
                return SyncPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "SyncPage";
            }
        });

        return links;
    }

    @Override
    public String getTagId() {
        return "Sync";
    }
}
