/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.web.pages;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.model.ResourceModel;
import org.odlabs.wiquery.core.commons.CoreJavaScriptResourceReference;

/**
 *
 * @author Artem
 */
public final class SessionExpiredPage extends PageExpiredErrorPage {
    public SessionExpiredPage() {
        super ();

        add(JavascriptPackageResource.getHeaderContribution(CoreJavaScriptResourceReference.get()));
        add(JavascriptPackageResource.getHeaderContribution("js/common.js"));
        add(JavascriptPackageResource.getHeaderContribution(SessionExpiredPage.class, "SessionExpiredPage.js"));
        add(new Label("title", new ResourceModel("session_expired.title")));
        add(CSSPackageResource.getHeaderContribution("css/style.css"));
    }
}

