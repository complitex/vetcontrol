package org.vetcontrol.sync.client.web.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.sync.client.service.BookSyncBean;
import org.vetcontrol.sync.client.service.UserSyncBean;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.2010 21:34:39
 */
public class TestPage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(TestPage.class);

    @EJB(name = "UserSyncBean")
    private UserSyncBean userSyncBean;

    @EJB(name = "BookSyncBean")
    private BookSyncBean bookSyncBean;


    @EJB(name = "ClientBean")
    private ClientBean clientBean;

    public TestPage() {

        add(new Form("test"){
            @Override
            protected void onSubmit() {
                try {
                    userSyncBean.process();
                    
                    bookSyncBean.setInitial(true);                    
                    bookSyncBean.process();
                } catch (NotRegisteredException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
