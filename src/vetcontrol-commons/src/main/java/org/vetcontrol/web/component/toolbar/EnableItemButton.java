/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.web.component.toolbar;

import org.apache.wicket.ResourceReference;

/**
 *
 * @author Artem
 */
public abstract class EnableItemButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-hideItem-1.gif";
    private static final String TITLE_KEY = "image.title.enableItem";

    public EnableItemButton(String id) {
        super(id, new ResourceReference(IMAGE_SRC), TITLE_KEY);
    }

}
