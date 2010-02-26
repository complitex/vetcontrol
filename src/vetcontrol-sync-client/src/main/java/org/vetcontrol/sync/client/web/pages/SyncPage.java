package org.vetcontrol.sync.client.web.pages;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.sync.client.service.BookSyncBean;
import org.vetcontrol.sync.client.service.ISyncListener;
import org.vetcontrol.sync.client.service.SyncEvent;
import org.vetcontrol.sync.client.service.UserSyncBean;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.02.2010 1:48:06
 */
public class SyncPage extends TemplatePage{
    @EJB(name = "UserSyncBean")
    UserSyncBean userSyncBean;

    @EJB(name = "BookSyncBean")
    BookSyncBean bookSyncBean;

    public SyncPage() {
        super();

        add(new Label("title", getString("sync.client.sync_page.title")));
        add(new FeedbackPanel("messages"));

        final IModel<String> model = new Model<String>("");

        MultiLineLabel label = new MultiLineLabel("label", model);
        label.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
        add(label);

        //Форма
        add(new Form("form"){
            @Override
            protected void onSubmit() {
                try {                         
                    model.setObject("");

                    userSyncBean.process();
                    bookSyncBean.process();
                } catch (NotRegisteredException e) {
                    error(e.getMessage());
                }
            }
        });

        ISyncListener listener = new ISyncListener(){

            @Override
            public void start(SyncEvent syncEvent) {
                model.setObject(model.getObject() + "Start: " + syncEvent.getObject() +"\n");
            }

            @Override
            public void sync(SyncEvent syncEvent) {
//                model.setObject(model.getObject() + "Sync: " + syncEvent.getIndex() + "/" + syncEvent.getCount() +"\n");
            }

            @Override
            public void complete(SyncEvent syncEvent) {
                model.setObject(model.getObject() + "Done: " + syncEvent.getObject() +"\n");
            }
        };

        userSyncBean.setSyncListener(listener);

        bookSyncBean.setInitial(true);
        bookSyncBean.setSyncListener(listener);
    }
}
