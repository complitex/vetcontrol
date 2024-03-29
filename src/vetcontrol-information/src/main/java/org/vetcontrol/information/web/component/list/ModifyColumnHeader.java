package org.vetcontrol.information.web.component.list;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ModifyColumnHeader extends Panel {

    private static final Logger log = LoggerFactory.getLogger(ModifyColumnHeader.class);

    public ModifyColumnHeader(String id, final Class bookClass, final IBookDataProvider dataProvider) {
        super(id);
        Link clear = new Link("clear") {

            @Override
            public void onClick() {
                try {
                    Form<?> form = getForm(this);
                    Object filterBean = bookClass.newInstance();
                    refreshFormComponentInput(form);
                    refreshModel(form, filterBean);
                    dataProvider.initSize();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        add(clear);
    }

    private static void refreshFormComponentInput(Form form) {
        form.clearInput();
    }

    private static void refreshModel(Form form, Object newModelObject) {
        form.modelChanging();
        form.getModel().setObject(newModelObject);
        form.modelChanged();
    }

    private static Form<?> getForm(Component component) {
        Form<?> form = Form.findForm(component);
        if (form == null) {
            throw new RuntimeException("Could not find Form parent for " + component);
        }
        return form;
    }
}
