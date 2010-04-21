/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component;

import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public class DatePicker<T> extends org.odlabs.wiquery.ui.datepicker.DatePicker<T> {

    private static final String IMAGE_SRC = "resources/" + Application.class.getName() + "/images/calendar.gif";

    public DatePicker(String id) {
        super(id);
        init();
    }

    public DatePicker(String id, Class<T> type) {
        super(id, type);
        init();
    }

    public DatePicker(String id, IModel<T> model) {
        super(id, model);
        init();
    }

    public DatePicker(String id, IModel<T> model, Class<T> type) {
        super(id, model, type);
        init();
    }

    protected void init() {
        setButtonImage(IMAGE_SRC);
        setShowOn(ShowOnEnum.BOTH);
        setButtonImageOnly(true);
        setDateFormat("dd.mm.yy");
    }
}
