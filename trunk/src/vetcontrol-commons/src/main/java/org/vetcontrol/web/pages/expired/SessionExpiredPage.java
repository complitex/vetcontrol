/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages.expired;

import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.odlabs.wiquery.core.commons.CoreJavaScriptResourceReference;
import org.vetcontrol.web.VetcontrolWebApplication;
import org.vetcontrol.web.resource.WebCommonResourceInitializer;

/**
 *
 * @author Artem
 */
public final class SessionExpiredPage extends WebPage {

    public SessionExpiredPage() {
        init();
    }

    private void init() {
        add(JavascriptPackageResource.getHeaderContribution(CoreJavaScriptResourceReference.get()));
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.COMMON_JS));
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", new ResourceModel("session_expired.title")));
        add(new Link("homePageLink") {

            @Override
            public void onClick() {
                ((VetcontrolWebApplication) getApplication()).logout();
            }
        });
    }

    /**
     * @see org.apache.wicket.markup.html.WebPage#configureResponse()
     */
    @Override
    protected void configureResponse() {
        super.configureResponse();
        getWebRequestCycle().getWebResponse().getHttpServletResponse().setStatus(
                HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * @see org.apache.wicket.Component#isVersioned()
     */
    @Override
    public boolean isVersioned() {
        return false;
    }

    /**
     * @see org.apache.wicket.Page#isErrorPage()
     */
    @Override
    public boolean isErrorPage() {
        return true;
    }
}

