/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.information.web.util.ResourceUtil;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.book.BookTypes;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ITemplateMenu;

import java.util.*;
import org.vetcontrol.information.web.util.BookWebInfoContainer;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public class BookMenu implements ITemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return ResourceUtil.getString(ResourceUtil.COMMON_RESOURCES_BUNDLE, "book.menu.title", locale, true);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        for (final Class bookType : BookTypes.all()) {
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return new DisplayBookClassModel(bookType).getObject();
                }

                @Override
                public Class<? extends Page> getPage() {
                    return BookWebInfoContainer.getListPage(bookType);
                }

                @Override
                public PageParameters getParameters() {
                    return BookWebInfoContainer.getListPageParameters(bookType);
                }

                @Override
                public String getTagId() {
                    return bookType.getSimpleName();
                }
            });
        }

        Collections.sort(links, new Comparator<ITemplateLink>() {

            @Override
            public int compare(ITemplateLink o1, ITemplateLink o2) {
                return o1.getLabel(locale).compareTo(o2.getLabel(locale));
            }
        });

        return links;
    }

    @Override
    public String getTagId() {
        return "books_menu";
    }
}
