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
public abstract class AddUserButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addUser.gif";
    private static final String TITLE_KEY = "image.title.addUser";

    public AddUserButton(String id) {
        super(id, new ResourceReference(AddItemButton.class, IMAGE_SRC), TITLE_KEY);
    }
}
