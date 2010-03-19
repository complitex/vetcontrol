/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Artem
 */
public final class Spacer extends Panel {

    public Spacer(String id) {
        super(id);
        init();
    }

    private void init() {
        add(new Image("image", new ResourceReference("images/spacer.gif")));
    }
}
