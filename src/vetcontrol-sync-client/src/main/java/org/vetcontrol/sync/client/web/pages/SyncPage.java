package org.vetcontrol.sync.client.web.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import org.vetcontrol.sync.client.service.ISyncListener;
import org.vetcontrol.sync.client.service.SyncBean;
import org.vetcontrol.sync.client.service.SyncEvent;
import org.vetcontrol.sync.client.service.SyncStatus;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.02.2010 1:48:06
 */
@AuthorizeInstantiation(SecurityRoles.AUTHORIZED)
public class SyncPage extends TemplatePage{
    private static final Logger log = LoggerFactory.getLogger(SyncPage.class);

    @EJB(name = "SyncBean")
    SyncBean syncBean;

    transient Future<SyncStatus> future;

    private class StatusRow implements Serializable{
        private Date date;
        private String name;
        private String status;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public SyncPage() {
        super();

        //Ejb-Async-Thread Support
        final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.client.web.pages.SyncPage", getLocale());

        add(new Label("title", getString("sync.client.sync_page.title")));
        add(new FeedbackPanel("messages"));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setVisible(false);
        add(container);

        final ListView<StatusRow> listView = new ListView<StatusRow>("list", new ArrayList<StatusRow>()){

            @Override
            protected void populateItem(ListItem<StatusRow> item) {
                item.add(DateLabel.forDatePattern("date", new Model<Date>(item.getModelObject().date), "dd.MM.yy HH:mm:ss"));
                item.add(new Label("name", item.getModelObject().name));
                item.add(new Label("status", item.getModelObject().status));
            }
        };        
        container.add(listView);

        final AjaxSelfUpdatingTimerBehavior timer = new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                if (future.isDone()){
                    StatusRow row = new StatusRow();
                    row.setStatus("Синхронизация завершена успешно");
                    listView.getModelObject().add(row);                                       

                    this.stop();
                }
            }
        };

        //Форма
        Form form = new Form("form");
        add(form);

        Button submit = new Button("submit"){
            @Override
            public void onSubmit() {
                try {
                    container.setVisible(true);
                    container.add(timer);
                    setVisible(false);

                    future = syncBean.asynchronousProcess();
                } catch (ExecutionException e) {
                    error(e.getCause().getLocalizedMessage());                    
                }
            }
        };
        form.add(submit);

        Button cancel = new Button("cancel"){
            @Override
            public void onSubmit() {
                if (future != null){
                    future.cancel(true);
                }
            }
        };
        form.add(cancel);
        cancel.setVisible(false);

        //Слушатель процесса синхронизации
        ISyncListener listener = new ISyncListener(){

            @Override
            public void start(SyncEvent syncEvent) {
                StatusRow row = new StatusRow();
                String key = ((Class)syncEvent.getObject()).getCanonicalName();
                row.setDate(DateUtil.getCurrentDate());
                row.setName(rb.containsKey(key) ? rb.getString(key) : key);
                row.setStatus("Начало синхронизации (" + syncEvent.getCount() + ")") ;

                listView.getModelObject().add(row);
            }

            @Override
            public void sync(SyncEvent syncEvent) {
                StatusRow row = listView.getModelObject().get(listView.getModelObject().size()-1);
                row.setDate(DateUtil.getCurrentDate());
                row.setStatus("Синхронизация: " + syncEvent.getIndex() + " из " + syncEvent.getCount());
            }

            @Override
            public void complete(SyncEvent syncEvent) {
                StatusRow row = listView.getModelObject().get(listView.getModelObject().size()-1);
                row.setDate(DateUtil.getCurrentDate());
                row.setStatus("Успешно. (" + syncEvent.getCount() + " элементов)");
            }
        };

        syncBean.setSyncListener(listener);
    }
}
