package org.vetcontrol.sync.client.web.pages;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.sync.client.service.RegistrationBean;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.util.Locale;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.pages.login.Login;
import org.vetcontrol.web.resource.WebCommonResourceInitializer;

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

    public RegistrationPage() {
        super();

        final Locale system = localeDAO.systemLocale();

        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", getString("sync.client.registration.title")));

        add(new FeedbackPanel("messages"));

        IModel<Client> clientModel = new Model<Client>(new Client());

        //DEBUG
        info("IP: " + clientBean.getCurrentIP() + ", MAC:" + clientBean.getCurrentMAC());

        //Регистрация успешно
        final WebMarkupContainer info = new WebMarkupContainer("info");
        add(info);
        info.setVisible(false);

        info.add(new Label("registered", getString("sync.client.registration.registered")));

        BookmarkablePageLink<Void> loginLink = new BookmarkablePageLink<Void>("login", Login.class);
        info.add(loginLink);


        //Форма регистрации
        Form form = new Form<Client>("registration_form", clientModel) {

            @Override
            protected void onSubmit() {
                Client client = getModelObject();

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

                try {
                    client = registrationBean.processRegistration(client);
                    log.debug("Клиент зарегистрирован. {}", client.toString());

                    setVisible(false);
                    info.setVisible(true);
                } catch (EJBException e) {
                    if (e.getCausedByException() instanceof UniformInterfaceException) {
                        UniformInterfaceException uie = (UniformInterfaceException) e.getCausedByException();
                        String message = uie.getResponse().getEntity(String.class);
                        log.error(message, e);
                        error(message);
                    } else {
                        log.error(e.getCausedByException().getLocalizedMessage(), e);
                        error(getString("sync.client.registration.error"));
                    }
                }
            }
        };
        add(form);

        DropDownChoice ddc = new DropDownChoice<Department>("sync.client.registration.department",
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
        ddc.setRequired(true);

        form.add(ddc);

        form.add(new TextField<String>("sync.client.registration.key", new PropertyModel<String>(clientModel, "secureKey")));

        add(new Spacer("spacer"));
    }
}
