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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.service.UIPreferences;
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
import java.util.List;
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

    private static boolean lock = false;

    private final String STATUS_MODEL = "STATUS_MODEL";

    @EJB(name = "SyncBean")
    SyncBean syncBean;

    transient Future<SyncStatus> future;
    private Button submit;
    private Form form;

    private class StatusRow implements Serializable{
        private Date date;
        private String name;
        private String status;
    }

    public SyncPage() {
        super();

        //Ejb-Async-Thread Support
        final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.client.web.pages.SyncPage", getLocale());

        add(new Label("title", getString("sync.client.sync_page.title")));
        add(new FeedbackPanel("messages"));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        final ListView<StatusRow> listView = new ListView<StatusRow>("list"){

            @Override
            protected void populateItem(ListItem<StatusRow> item) {
                item.add(DateLabel.forDatePattern("date", new Model<Date>(item.getModelObject().date), "dd.MM.yy HH:mm:ss"));
                item.add(new Label("name", item.getModelObject().name));
                item.add(new Label("status", item.getModelObject().status));
            }
        };
        container.add(listView);


        //Загрузка информации о статусе предыдущей синхронизации
        @SuppressWarnings({"unchecked"})
        IModel<List<StatusRow>> model = getPreferences().getPreference(UIPreferences.PreferenceType.SYNC, STATUS_MODEL, IModel.class);
        if (model != null){
            listView.setModel(model);
            container.setVisible(true);
        }else{
            model = new ListModel<StatusRow>(new ArrayList<StatusRow>());
            listView.setModel(model);
            container.setVisible(false);
            getPreferences().putPreference(UIPreferences.PreferenceType.SYNC, STATUS_MODEL, model);
        }

        //Форма
        form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        submit = new Button("submit"){
            @Override
            public void onSubmit() {
                try {
                    if (lock){
                        error("Синхронизация уже запущена в фоновом режиме или другом окне браузера");
                        return;
                    }

                    container.removeAll();
                    container.setVisible(true);

                    listView.getModelObject().clear();
                    container.add(listView);

                    container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
                        @Override
                        protected void onPostProcessTarget(AjaxRequestTarget target) {
                            if (future.isDone()){
                                StatusRow row = new StatusRow();
                                row.status = "Синхронизация завершена успешно.";
                                listView.getModelObject().add(row);

                                submit.setVisible(true);
                                target.addComponent(form);

                                this.stop();
                            }
                        }
                    });
                    setVisible(false);

                    StatusRow row = new StatusRow();
                    row.date = DateUtil.getCurrentDate();
                    row.status = "Начало синхронизации";
                    listView.getModelObject().add(row);

                    future = syncBean.asynchronousProcess();
                } catch (ExecutionException e) {
                    error(e.getCause().getLocalizedMessage());
                    lock = false;
                }
            }
        };
        form.add(submit);

        //Слушатель процесса синхронизации
        ISyncListener listener = new ISyncListener(){

            @Override
            public void start(SyncEvent syncEvent) {
                lock = true;

                StatusRow row = new StatusRow();
                String key = ((Class)syncEvent.getObject()).getCanonicalName();
                row.date = DateUtil.getCurrentDate();
                row.name = rb.containsKey(key) ? rb.getString(key) : key;
                row.status = "Подключение к серверу... " + syncEvent.getCount();

                listView.getModelObject().add(row);
            }

            @Override
            public void sync(SyncEvent syncEvent) {
                StatusRow row = listView.getModelObject().get(listView.getModelObject().size()-1);
                row.date = DateUtil.getCurrentDate();
                row.status = "Синхронизация... " + syncEvent.getIndex() + " из " + syncEvent.getCount();
            }

            @Override
            public void complete(SyncEvent syncEvent) {
                StatusRow row = listView.getModelObject().get(listView.getModelObject().size()-1);
                row.date = DateUtil.getCurrentDate();
                row.status = "Успешно синхронизировано: " + syncEvent.getCount();

                lock = false;
            }
        };

        syncBean.setSyncListener(listener);
    }
}
