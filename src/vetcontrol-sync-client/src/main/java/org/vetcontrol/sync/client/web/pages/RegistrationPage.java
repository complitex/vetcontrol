package org.vetcontrol.sync.client.web.pages;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.sync.client.service.RegistrationBean;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.02.2010 18:18:28
 */
public class RegistrationPage extends WebPage {
     private static final Logger log = LoggerFactory.getLogger(RegistrationPage.class);

    @EJB(name = "RegistrationBean")
    private RegistrationBean registrationBean;

    public RegistrationPage() {
        super();

        add(new FeedbackPanel("messages"));

        add(new Form("registration_form"){
            @Override
            protected void onSubmit() {
                Client client = new Client();

                try {
                    client.setIp(InetAddress.getLocalHost().getHostAddress());

                    NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

                    String mac = "";
                    if (networkInterface.getHardwareAddress() != null){
                        for (byte m  : networkInterface.getHardwareAddress()){
                            mac += String.format("%02x-", m);
                        }
                    }
                    if (!mac.isEmpty()){
                        mac = mac.substring(0, mac.length()-1).toUpperCase();
                        client.setMac(mac);
                    }    
                } catch (SocketException e) {
                    log.error(e.getMessage(), e);
                    error(e.getLocalizedMessage());
                } catch (UnknownHostException e) {
                    log.error(e.getMessage(), e);
                    error(e.getLocalizedMessage());
                }

                List<Department> departments = registrationBean.getDepartments();

                client.setDepartment(departments.get(0));

                try {
                    client = registrationBean.processRegistration(client);
                    info("Клиент зарегистрирован. " + client.toString());
                    log.info("Клиент зарегистрирован. {}", client.toString());
                } catch (EJBException e) {
                    if (e.getCausedByException() instanceof UniformInterfaceException){
                        UniformInterfaceException uie = (UniformInterfaceException) e.getCausedByException();
                        String message = uie.getResponse().getEntity(String.class);
                        log.error(message, e);
                        error(message);
                    }
                }
            }
        });
    }
}
