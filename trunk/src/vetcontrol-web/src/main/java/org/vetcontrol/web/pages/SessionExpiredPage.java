/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.web.pages;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.model.ResourceModel;

/**
 *
 * @author Artem
 */
public final class SessionExpiredPage extends PageExpiredErrorPage {
    public SessionExpiredPage() {
        super ();
        
        add(new Label("title", new ResourceModel("session_expired.title")));
    }
}

