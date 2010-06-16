/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.logging.web.component.change;

import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Artem
 */
public final class DetailsLink extends Panel {

    public DetailsLink(String id) {
        super(id);
        init();
    }

    private void init() {
        add(JavascriptPackageResource.getHeaderContribution(DetailsLink.class, DetailsLink.class.getSimpleName() + ".js"));
        add(new Link("detailsLink") {

            @Override
            public void onClick() {
            }

            @Override
            protected CharSequence getURL() {
                return "#";
            }
        });
    }
}
