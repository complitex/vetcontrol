package org.vetcontrol.sync;

import org.vetcontrol.entity.Client;

import javax.jws.WebService;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.02.2010 15:45:08
 */
@WebService
public interface IRegistrationService {

    public Client processRegistration(Client client) throws RegistrationException;

    
}
