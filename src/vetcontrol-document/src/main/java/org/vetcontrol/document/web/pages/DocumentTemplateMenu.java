package org.vetcontrol.document.web.pages;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ResourceTemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.vetcontrol.web.security.SecurityRoles.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 12:08:10
 */
@AuthorizeInstantiation({DOCUMENT_CREATE, DOCUMENT_DEP_VIEW, DOCUMENT_DEP_CHILD_VIEW})
public class DocumentTemplateMenu extends ResourceTemplateMenu {
    @Override
    public String getTitle(Locale locale) {
        return getString(DocumentTemplateMenu.class, locale, "document.template.menu.title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(DocumentTemplateMenu.class, locale, "document.template.menu.cargo.list");
            }
            @Override
            public Class<? extends Page> getPage() {
                return DocumentCargoList.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "DocumentCargoList";
            }

        });

        return links;
    }

    @Override
    public String getTagId() {
        return "document_menu";
    }


}
