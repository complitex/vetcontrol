/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.list;

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.util.book.entity.ShowBooksMode;

/**
 *
 * @author Artem
 */
public final class ShowBooksModePanel extends Panel {

    private IModel<ShowBooksMode> model;

    public ShowBooksModePanel(String id, IModel<ShowBooksMode> model) {
        super(id);
        this.model = model;
        init();
    }

    private void init() {
        IChoiceRenderer renderer = new EnumChoiceRenderer(this);
        List<ShowBooksMode> choices = Arrays.asList(ShowBooksMode.values());
        RadioChoice showBooksMode = new RadioChoice("showBooksMode", model, choices, renderer);
        showBooksMode.setSuffix("");
        add(showBooksMode);
    }
}
