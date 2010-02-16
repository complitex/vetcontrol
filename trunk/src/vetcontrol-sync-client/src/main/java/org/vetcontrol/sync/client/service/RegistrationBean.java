package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.Synchronized;
import org.vetcontrol.sync.JSONResolver;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.sun.jersey.api.client.Client.create;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.02.2010 16:09:37
 */
@Stateless(name = "RegistrationBean")
public class RegistrationBean {
    private static final Logger log = LoggerFactory.getLogger(RegistrationBean.class);

    @PersistenceContext
    private EntityManager em;

    public Client processRegistration(Client client){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JSONResolver.JAXBContextResolver.class);
        clientConfig.getClasses().add(JSONResolver.UnmarshallerContextResolver.class);

        String syncServerUrl = ":)";

        try {
            //FIX inject by @Resource(name = "syncServerUrl") return null
            syncServerUrl = (String) new InitialContext().lookup("java:module/env/syncServerUrl");
        } catch (NamingException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        
        client =  create(clientConfig)
                .resource(syncServerUrl+"/registration")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(Client.class, client);

        em.createQuery("delete from Client c where c.mac = :mac")
                .setParameter("mac", client.getMac())
                .executeUpdate();

        //commit
        create(clientConfig)
                .resource(syncServerUrl+"/registration/commit")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(client.getSecureKey());
        client.setSyncStatus(Synchronized.SyncStatus.SYNCHRONIZED);

        client.getInsertQuery(em).executeUpdate();

        return client;
    }

    public List<Department> getDepartments(){
        return em.createQuery("select d from Department d", Department.class).getResultList();
    }
}
