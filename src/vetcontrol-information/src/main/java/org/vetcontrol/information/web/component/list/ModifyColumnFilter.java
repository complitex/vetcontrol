package org.vetcontrol.information.web.component.list;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

class ModifyColumnFilter extends Panel {

    public ModifyColumnFilter(String id, final IBookDataProvider dataProvider) {
        super(id);
        Button goSearch = new Button("filter"){

            @Override
            public void onSubmit() {
                dataProvider.initSize();
            }

        };
        add(goSearch);
    }
}
