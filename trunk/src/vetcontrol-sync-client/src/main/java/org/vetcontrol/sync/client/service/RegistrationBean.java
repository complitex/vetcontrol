package org.vetcontrol.sync.client.service;

import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.Synchronized;
import org.vetcontrol.util.DateUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.02.2010 16:09:37
 */
@Stateless(name = "RegistrationBean")
public class RegistrationBean {
    @PersistenceContext
    private EntityManager em;

    public Client processRegistration(Client client){
        client =  createJSONClient("/registration").post(Client.class, client);

        em.createQuery("delete from Client c where c.mac = :mac")
                .setParameter("mac", client.getMac())
                .executeUpdate();

        //commit        
        client.setSyncStatus(Synchronized.SyncStatus.SYNCHRONIZED);
        client.setUpdated(DateUtil.getCurrentDate());

        client.getInsertQuery(em).executeUpdate();

        createJSONClient("/registration/commit").put(client.getSecureKey());

        return client;
    }

    public List<Department> getDepartments(){
        return em.createQuery("select d from Department d", Department.class).getResultList();
    }
}
