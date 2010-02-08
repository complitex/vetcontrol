package org.vetcontrol.sync.client.service;

import org.vetcontrol.sync.IRegistrationService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import java.net.URL;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.02.2010 13:42:56
 */
@WebServiceClient(name = "RegistrationService",
        wsdlLocation = "http://localhost:8080/server/RegistrationService?wsdl",
        targetNamespace = "http://sync.vetcontrol.org")
public class RegistrationServiceClient extends Service{

    public RegistrationServiceClient(URL wsdlDocumentLocation, QName serviceName) {
        super(wsdlDocumentLocation, serviceName);
    }

    @WebEndpoint(name = "RegistrationPort")
    public IRegistrationService getRegistrationPort() {
        return getPort(new QName("http://sync.vetcontrol.org", "RegistrationPort"), IRegistrationService.class);
    }
}
