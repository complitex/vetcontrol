package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.sync.JSONResolver;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.02.2010 16:09:37
 */
@Stateless(name = "RegistrationBean")
public class RegistrationBean {
    @PersistenceContext
    private EntityManager em;

    public Client processRegistration(Client client){
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JSONResolver.JAXBContextResolver.class);
        cc.getClasses().add(JSONResolver.UnmarshallerContextResolver.class);

        return com.sun.jersey.api.client.Client.create(cc)
                .resource("http://localhost:8080/server/sync/registration")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(Client.class, client);
    }
    
    public List<Department> getDepartments(){
        return em.createQuery("select d from Department d", Department.class).getResultList();
    }
}
