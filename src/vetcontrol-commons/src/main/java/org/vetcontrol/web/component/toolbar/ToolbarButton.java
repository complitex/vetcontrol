/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component.toolbar;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.ResourceModel;

/**
 *
 * @author Artem
 */
public abstract class ToolbarButton extends Panel {

    public ToolbarButton(String id, ResourceReference imageSrc, String titleKey) {
        super(id);

        Link link = addLink();
        Image image = addImage(imageSrc, new ResourceModel(titleKey).wrapOnAssignment(this));
        link.add(image);

        add(link);
    }

    protected abstract void onClick();

    protected Link addLink() {
        return new Link("link") {

            @Override
            public void onClick() {
                ToolbarButton.this.onClick();
            }
        };
    }

    protected Image addImage(ResourceReference imageSrc, final IWrapModel<String> title) {
        return new Image("image", imageSrc) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("title", title.getObject());
            }
        };
    }
}
