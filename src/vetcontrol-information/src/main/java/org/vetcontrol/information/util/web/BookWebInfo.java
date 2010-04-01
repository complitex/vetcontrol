/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.util.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

/**
 *
 * @author Artem
 */
public class BookWebInfo {

    private Class<? extends WebPage> listPage;
    private Class<? extends WebPage> editPage;
    private PageParameters listPageParameters;

    public BookWebInfo(Class<? extends WebPage> listPage, Class<? extends WebPage> editPage, PageParameters listPageParameters) {
        this.listPage = listPage;
        this.editPage = editPage;
        this.listPageParameters = listPageParameters;
    }

    public Class<? extends WebPage> getEditPage() {
        return editPage;
    }

    public Class<? extends WebPage> getListPage() {
        return listPage;
    }

    public PageParameters getListPageParameters() {
        return listPageParameters;
    }
}
