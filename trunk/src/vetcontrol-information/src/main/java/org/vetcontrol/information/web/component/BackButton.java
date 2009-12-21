/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.web.component;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Artem
 */
public class BackButton extends Panel {
    

    public BackButton(String id, final Class<? extends WebPage> page) {
        super(id);

        add(new SubmitLink("cancel"){

            @Override
            public void onSubmit() {
                setResponsePage(page);
            }

        }.setDefaultFormProcessing(false));
    }

  


}
