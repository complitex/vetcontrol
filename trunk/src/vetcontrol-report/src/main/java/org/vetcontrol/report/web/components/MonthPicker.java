/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.web.components;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.vetcontrol.report.util.movementtypes.Month;

/**
 *
 * @author Artem
 */
public final class MonthPicker extends Panel {
    public MonthPicker(String id, final IModel<Month> model) {
        super (id);

        List<Month> choices = Arrays.asList(Month.JAN, Month.FEB, Month.MAR, Month.APR, Month.MAY, Month.JUN,
        Month.JUL, Month.AUG, Month.SEP, Month.OCT, Month.NOV, Month.DEC);

        IChoiceRenderer<Month> renderer = new IChoiceRenderer<Month>() {

            @Override
            public Object getDisplayValue(Month month) {
                return month.getDisplayName(getLocale());
            }

            @Override
            public String getIdValue(Month month, int index) {
                return String.valueOf(month.getNumber());
            }
        };

        DropDownChoice<Month> select = new DropDownChoice<Month>("select", model, choices, renderer);
        select.setRequired(true);
        add(select);
    }
}
