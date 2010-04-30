package org.vetcontrol.sync.client.web.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.sync.client.service.SyncBean;
import org.vetcontrol.sync.client.service.SyncMessage;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.02.2010 1:48:06
 */
@AuthorizeInstantiation(SecurityRoles.AUTHORIZED)
public class SyncPage extends TemplatePage{
    private static final Logger log = LoggerFactory.getLogger(SyncPage.class);

    @EJB(name = "SyncBean")
    SyncBean syncBean;

    private final Button submit;
    private final ListView<SyncMessage> listView;
    private final Form form;
    private final WebMarkupContainer container; 

    public SyncPage() {
        super();

        setStatelessHint(false);

        add(new Label("title", getString("sync.client.sync_page.title")));
        add(new FeedbackPanel("messages"));

        //Контейнер для ajax обновления компонента ListView
        container = new WebMarkupContainer("container");
        add(container);

        //Дата последней успешной синхронизации
        Date lastSync = syncBean.getLastSync();
        if (lastSync != null){
            add(DateLabel.forDatePattern("lastSync", new Model<Date>(lastSync), "dd.MM.yy HH:mm:ss"));
        }else{
            add(new Label("lastSync", getString("sync.client.last_sync.null")));
        }

        //Статус процесса синхронизации
        listView = new ListView<SyncMessage>("list", syncBean.getSyncMessages()){

            @Override
            protected void populateItem(ListItem<SyncMessage> item) {
                try {
                    item.add(DateLabel.forDatePattern("date", new Model<Date>(item.getModelObject().getDate()), "dd.MM.yy HH:mm:ss"));
                    item.add(new Label("name", item.getModelObject().getName()));
                    item.add(new Label("message", item.getModelObject().getMessage()));
                } catch (Exception e) {
                    //nothing
                }
            }
        };
        container.add(listView);
        container.setVisible(!listView.getModelObject().isEmpty());

        //Форма
        form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        submit = new Button("submit"){
            @Override
            public void onSubmit() {
                container.removeAll();
                container.add(listView);
                container.setVisible(true);

                //Асинхронный процесс синхронизации
                if (!syncBean.isProcessing()){
                    syncBean.asynchronousProcess(getLocale());
                }else{
                    info("Синхронизация уже была запущена в фоновом режиме или другом окне браузера");
                }

                listView.setModelObject(syncBean.getSyncMessages());
                container.add(newAjaxTimer());
                setVisible(false);
            }
        };
        form.add(submit);

        //Если синхронизация уже запущена, то запустить таймер обновления статуса
        if (syncBean.isProcessing()){
            container.add(newAjaxTimer());
            submit.setVisible(false);
        }
    }

    private IBehavior newAjaxTimer(){
        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                listView.setModelObject(syncBean.getSyncMessages());

                if (!syncBean.isProcessing()){                    
                    setRedirect(true);
                    setResponsePage(SyncPage.class);

                    this.stop();
                }
            }
        };
    }
}
