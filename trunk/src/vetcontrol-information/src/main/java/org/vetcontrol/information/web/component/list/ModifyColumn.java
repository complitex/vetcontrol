package org.vetcontrol.information.web.component.list;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

class ModifyColumn implements IFilteredColumn, IColumn {

    private Class bookClass;
    private ISelectable selectable;

    public ModifyColumn(Class bookClass, ISelectable selectable) {
        this.bookClass = bookClass;
        this.selectable = selectable;
    }

    @Override
    public void populateItem(Item cellItem, String componentId, IModel rowModel) {
        cellItem.add(new EditPanel(componentId, rowModel, selectable));
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
}
