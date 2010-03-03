/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.web.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author Artem
 */
public class RowNumberLabel extends Label {

    public RowNumberLabel(String id, final Item<?> item) {
        super(id, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                DataView<?> dataView = (DataView) item.getParent();
                if (dataView == null) {
                    throw new RuntimeException("NULL");
                }
                String rowNumberLabel = String.valueOf(getViewOffset(dataView) + item.getIndex() + 1);
                return rowNumberLabel;
            }
        });
    }

    private static int getViewOffset(DataView<?> dataView) {
        return dataView.getCurrentPage() * dataView.getItemsPerPage();
    }
}
