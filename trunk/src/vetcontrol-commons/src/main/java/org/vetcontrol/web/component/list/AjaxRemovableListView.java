/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.component.list;

import java.io.Serializable;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Artem
 */
public abstract class AjaxRemovableListView<T extends Serializable> extends ListView<T> {

    public AjaxRemovableListView(String id, List<? extends T> list) {
        super(id, list);
        setReuseItems(true);
    }

    @Override
    protected IModel<T> getListItemModel(IModel<? extends List<T>> listViewModel, int index) {
        return new Model<T>(listViewModel.getObject().get(index));
    }

    protected AjaxLink addRemoveLink(String linkId, ListItem<T> item, Component toFocus, Component... toUpdate) {
        AjaxLink removeLink = getRemoveLink(linkId, toFocus, toUpdate);
        item.add(removeLink);
        return removeLink;
    }

    protected AjaxLink getRemoveLink(String linkId, final Component toFocus, final Component... toUpdate) {
        return new AjaxLink(linkId) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                ListItem<T> item = (ListItem) getParent();
                ListView<T> list = (ListView<T>) item.getParent();

                int removeIndex = item.getIndex();
                int last_index = list.getModelObject().size() - 1;

                //Copy childs from next list item and remove last
                for (int index = item.getIndex(); index < last_index; index++) {
                    ListItem<T> li = (ListItem) item.getParent().get(index);
                    ListItem<T> li_next = (ListItem) item.getParent().get(index + 1);

                    li.removeAll();
                    if (li.getIndex() == removeIndex) {
                        for (int i = 0; i < li.size(); i++) {
                            li.get(i).remove();
                        }
                    }
                    li.setModelObject(li_next.getModelObject());

                    int size = li_next.size();
                    Component[] childs = new Component[size];
                    for (int i = 0; i < size; i++) {
                        childs[i] = li_next.get(i);
                    }
                    li.add(childs);
                }
                item.getParent().get(last_index).remove();

                list.getModelObject().remove(removeIndex);

                if (toFocus != null) {
                    target.focusComponent(toFocus);
                }

                for (Component comp : toUpdate) {
                    target.addComponent(comp);
                }
            }
        };
    }
}
