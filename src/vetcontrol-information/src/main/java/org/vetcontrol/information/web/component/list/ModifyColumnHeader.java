package org.vetcontrol.information.web.component.list;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

class ModifyColumnHeader extends Panel {

    public ModifyColumnHeader(String id, final Class bookClass) {
        super(id);
        Button clear = new Button("header") {

            @Override
            public void onSubmit() {
                try {
                    Object filterBean = bookClass.newInstance();
                    getForm().setDefaultModelObject(filterBean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        add(clear);
    }
}
