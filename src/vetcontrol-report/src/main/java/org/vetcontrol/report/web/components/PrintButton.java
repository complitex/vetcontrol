/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.components;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.link.Link;
import org.vetcontrol.report.util.jasper.ExportType;
import org.vetcontrol.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public class PrintButton extends ToolbarButton {

    private static final String IMAGE_SRC = "icon-print.gif";
    private static final String TITLE_PREFIX = "title.";
    private String formId;

    public PrintButton(String id, ExportType exportType, String formId) {
        super(id, new ResourceReference(PrintButton.class, IMAGE_SRC), TITLE_PREFIX + exportType.name().toLowerCase());
        this.formId = formId;
    }

    @Override
    protected Link addLink() {
        return new Link("link") {

            @Override
            public void onClick() {
            }

            @Override
            protected CharSequence getURL() {
                return "#";
            }

            @Override
            protected CharSequence getOnClickScript(CharSequence url) {
                return "$('#" + formId + "').submit();";
            }
        };
    }

    @Override
    protected void onClick() {
    }
}
