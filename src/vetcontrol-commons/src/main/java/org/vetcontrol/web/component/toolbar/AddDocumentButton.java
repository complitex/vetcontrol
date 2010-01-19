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
public abstract class AddDocumentButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addDocument.gif";
    private static final String TITLE_KEY = "image.title.addDocument";

    public AddDocumentButton(String id) {
        super(id, new ResourceReference(AddItemButton.class, IMAGE_SRC), TITLE_KEY);
    }
}
