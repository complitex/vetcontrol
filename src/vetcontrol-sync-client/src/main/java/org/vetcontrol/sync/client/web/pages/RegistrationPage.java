package org.vetcontrol.sync.client.web.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.sync.client.service.RegistrationBean;
import org.vetcontrol.sync.client.service.SyncMessage;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.pages.login.Login;
import org.vetcontrol.web.resource.WebCommonResourceInitializer;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.02.2010 18:18:28
 */
public class RegistrationPage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(RegistrationPage.class);

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "ClientBean")
    private ClientBean clientBean;

    @EJB(name = "RegistrationBean")
    private RegistrationBean registrationBean;

    private WebMarkupContainer info, formContainer;
    private Form form;
    private BookmarkablePageLink loginLink;

    public RegistrationPage() {
        super();

        final Locale system = localeDAO.systemLocale();

        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", getString("sync.client.registration.title")));

        FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setEscapeModelStrings(false);
        add(feedbackPanel);

        final IModel<Client> clientModel = new Model<Client>(new Client());

        //DEBUG
        info("IP: " + clientBean.getCurrentIP() + ", MAC:" + clientBean.getCurrentMAC());

        //Статус процесса регистрации
        info = new WebMarkupContainer("info");
        add(info);

        final ListView<SyncMessage> listView = new ListView<SyncMessage>("list",
                new LoadableDetachableModel<List<SyncMessage>>(){
                    @Override
                    protected List<SyncMessage> load() {
                        return registrationBean.getSyncMessages();
                    }
                }){
            @Override
            protected void populateItem(ListItem<SyncMessage> item) {
                try {
                    item.add(DateLabel.forDatePattern("date", new Model<Date>(item.getModelObject().getDate()), "dd.MM.yy HH:mm:ss"));
                    item.add(new Label("message", item.getModelObject().getMessage()));
                } catch (Exception e) {
                    //nothing
                }
            }
        };
        info.add(listView);

        if (registrationBean.isProcessing()){
            info.add(newAjaxTimer());
        }

        loginLink = new BookmarkablePageLink<Void>("login", Login.class);
        loginLink.setVisible(false);
        info.add(loginLink);

        formContainer = new WebMarkupContainer("form_container");
        formContainer.setOutputMarkupId(true);
        add(formContainer);

        //Форма регистрации
        form = new Form<Client>("registration_form", clientModel);
        form.setVisible(!registrationBean.isProcessing());
        formContainer.add(form);

        Button register = new Button("register"){
            @Override
            public void onSubmit() {
                Client client = clientModel.getObject();

                String ip = clientBean.getCurrentIP();
                if (ip != null) {
                    client.setIp(ip);
                } else {
                    error("Ошибка получения IP адреса клиента");
                }

                String mac = clientBean.getCurrentMAC();
                if (mac != null) {
                    client.setMac(mac);
                } else {
                    error("Ошибка получения MAC адреса клиента");
                }

                info.add(newAjaxTimer());

                form.setVisible(false);

                registrationBean.processRegistration(client, getLocale());

                listView.getModel().detach();
            }
        };
        form.add(register);

        //Подразделение
        DropDownChoice ddcDepartment = new DropDownChoice<Department>("sync.client.registration.department",
                new PropertyModel<Department>(clientModel, "department"),
                registrationBean.getDepartments(),
                new IChoiceRenderer<Department>() {

                    @Override
                    public Object getDisplayValue(Department department) {
                        return department.getDisplayName(getLocale(), system);
                    }

                    @Override
                    public String getIdValue(Department department, int index) {
                        return department.getId().toString();
                    }
                });
        ddcDepartment.setRequired(true);
        form.add(ddcDepartment);

        //Пункт пропуска через границу
        final DropDownChoice<PassingBorderPoint> ddcPassingBorderPoint =
                new DropDownChoice<PassingBorderPoint>("sync.client.registration.passing_border_point",
                        new PropertyModel<PassingBorderPoint>(clientModel, "passingBorderPoint"),
                        new LoadableDetachableModel<List<PassingBorderPoint>>() {

                            @Override
                            protected List<PassingBorderPoint> load() {
                                return registrationBean.getPassingBorderPoints(clientModel.getObject().getDepartment());
                            }
                        }, new IChoiceRenderer<PassingBorderPoint>() {

                            @Override
                            public Object getDisplayValue(PassingBorderPoint object) {
                                return object.getName();
                            }

                            @Override
                            public String getIdValue(PassingBorderPoint object, int index) {
                                return object.getId().toString();
                            }
                        });
        ddcPassingBorderPoint.setOutputMarkupId(true);
        form.add(ddcPassingBorderPoint);

        ddcDepartment.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(ddcPassingBorderPoint);
            }
        });

        form.add(new TextField<String>("sync.client.registration.key", new PropertyModel<String>(clientModel, "secureKey")));

        add(new Spacer("spacer"));
    }

    private IBehavior newAjaxTimer(){
        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                target.addComponent(info);

                if (registrationBean.isComplete()){
                    if (registrationBean.isError()){
                        form.setVisible(true);
                        target.addComponent(formContainer);
                    }else{
                        loginLink.setVisible(true);
                    }

                    this.stop();
                }
            }
        };
    }
}
