package org.vetcontrol.sync.client.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.vetcontrol.entity.Client;
import org.vetcontrol.sync.RegistrationException;
import org.vetcontrol.sync.client.service.RegistrationBean;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.02.2010 18:18:28
 */
public class RegistrationPage extends WebPage {
    @EJB(name = "RegistrationBean")
    private RegistrationBean registrationBean;

    public RegistrationPage() {
        super();

        add(new Form("registration_form"){
            @Override
            protected void onSubmit() {
                Client client = new Client();

                try {
                    System.out.println(registrationBean.processRegistration(client));
                } catch (RegistrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
