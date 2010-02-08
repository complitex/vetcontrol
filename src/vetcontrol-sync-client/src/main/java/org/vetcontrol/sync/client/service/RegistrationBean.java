package org.vetcontrol.sync.client.service;

import org.vetcontrol.entity.Client;
import org.vetcontrol.sync.IRegistrationService;
import org.vetcontrol.sync.RegistrationException;

import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.02.2010 16:09:37
 */
@Stateless(name = "RegistrationBean")
public class RegistrationBean {
    @WebServiceRef(RegistrationServiceClient.class)
    private IRegistrationService registrationService;

    public Client processRegistration(Client client) throws RegistrationException {
        registrationService.test(client.toString());

//        return registrationService.processRegistration(client);
        return  null;
    }

}
