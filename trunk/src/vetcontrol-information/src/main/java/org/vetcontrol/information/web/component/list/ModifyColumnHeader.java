package org.vetcontrol.information.web.component.list;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ModifyColumnHeader extends Panel {

    private final Logger log = LoggerFactory.getLogger(ModifyColumnHeader.class);

    public ModifyColumnHeader(String id, final Class bookClass) {
        super(id);
        Button clear = new Button("header") {

            @Override
            public void onSubmit() {
                try {
                    Form form = getForm();
                    Object filterBean = bookClass.newInstance();
                    refreshModel(form, filterBean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        add(clear);
    }

    private void refreshModel(Form form, Object newModelObject) {
        form.modelChanging();
        form.getModel().setObject(newModelObject);
        form.modelChanged();
    }
}
