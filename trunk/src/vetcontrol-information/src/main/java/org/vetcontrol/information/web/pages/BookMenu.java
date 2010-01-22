/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.web.pages;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.vetcontrol.information.util.web.ResourceUtil;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.information.web.support.BookTypes;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.ITemplateLink;
import org.vetcontrol.web.template.ITemplateMenu;

import java.util.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public class BookMenu implements ITemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return ResourceUtil.getString(ResourceUtil.BOOK_NAMES_BUNDLE, "book.menu.title", locale);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();
        for(final Class bookType : BookTypes.getList()){
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return new DisplayBookClassModel(bookType, locale).getObject();
                }

                @Override
                public Class<? extends Page> getPage() {
                    return BookPage.class;
                }

                @Override
                public PageParameters getParameters() {
                    PageParameters params = new PageParameters();
                    params.add(BookPage.BOOK_TYPE, bookType.getName());
                    return params;
                }

                @Override
                public String getTagId() {
                    return bookType.getSimpleName();
                }

            });
            Collections.sort(links, new Comparator<ITemplateLink>(){

                @Override
                public int compare(ITemplateLink o1, ITemplateLink o2) {
                    return o1.getLabel(locale).compareTo(o2.getLabel(locale));
                }
            });
        }

        return links;
    }

    @Override
    public String getTagId() {
        return "books_menu";
    }
}
