package org.vetcontrol.information.web.component.list;

import java.io.Serializable;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class ModifyColumn implements IFilteredColumn, IColumn {

    private Class bookClass;

    public ModifyColumn(Class bookClass) {
        this.bookClass = bookClass;
    }

    @Override
    public void populateItem(Item cellItem, String componentId, IModel rowModel) {
        cellItem.add(new EditPanel(componentId, rowModel) {

            @Override
            protected void selected(Serializable bean) {
                ModifyColumn.this.selected(bean);
            }
        });
    }

    @Override
    public Component getFilter(String componentId, FilterForm form) {
        Panel filter = new ModifyColumnFilter(componentId);
        return filter;
    }

    @Override
    public Component getHeader(String componentId) {
        Panel header = new ModifyColumnHeader(componentId, bookClass);
        return header;
    }

    @Override
    public String getSortProperty() {
        return null;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public void detach() {
    }

    protected abstract void selected(Serializable bean);
}
