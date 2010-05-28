package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.entity.Synchronized;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.util.DateUtil;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.02.2010 16:09:37
 */
@Singleton(name = "RegistrationBean")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class RegistrationBean {
    private static final Logger log = LoggerFactory.getLogger(RegistrationBean.class);

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    @EJB(beanName = "UserSyncBean")
    private UserSyncBean userSyncBean;

    private boolean processing, complete, error;

    private List<SyncMessage> syncMessages = new ArrayList<SyncMessage>();

    private ResourceBundle rb;

    @Asynchronous
    public Future<String> processRegistration(Client client, Locale locale){
        try {
            utx.begin();

            rb = ResourceBundle.getBundle("org.vetcontrol.sync.client.service.RegistrationBean", locale);

            processing = true;
            complete = false;
            error = false;

            syncMessages.clear();

            addMessage(rb.getString("sync.client.registration.start"));

            client =  createJSONClient("/registration").post(Client.class, client);

            em.createQuery("delete from Client c where c.mac = :mac")
                    .setParameter("mac", client.getMac())
                    .executeUpdate();

            client.setSyncStatus(Synchronized.SyncStatus.SYNCHRONIZED);
            client.setUpdated(DateUtil.getCurrentDate());
            client.setVersion("1.0.0");

            EntityPersisterUtil.executeInsert(em, client);

            addMessage(rb.getString("sync.client.registration.user_sync"));

            userSyncBean.processTxRequied();

            //commit

            addMessage(rb.getString("sync.client.registration.commit"));

            createJSONClient("/registration/commit").put(client.getSecureKey());

            addMessage(rb.getString("sync.client.registration.complete"));

            processing = false;
            complete = true;

            utx.commit();
        } catch (UniformInterfaceException e) {
            try {
                utx.rollback();
            } catch (SystemException e1) {
                log.error(e1.getLocalizedMessage(), e1);
            }

            processing = false;
            complete = true;
            error = true;

            String message = null;

            if (e.getResponse().getStatus() == 400) {
                message = e.getResponse().getEntity(String.class);
                addMessage(message);
            }else{
                addMessage(rb.getString("sync.client.registration.error"));
            }

            log.error(message != null ? message : e.getLocalizedMessage(), e);           
        } catch (Exception e){
            try {
                utx.rollback();
            } catch (SystemException e1) {
                log.error(e1.getLocalizedMessage(), e1);
            }

            processing = false;
            complete = true;
            error = true;

            addMessage(rb.getString("sync.client.registration.error"));

            log.error(e.getLocalizedMessage(), e);
        }

        return new AsyncResult<String>("COMPLETE");
    }

    private void addMessage(String message){
        syncMessages.add(new SyncMessage(message));
    }

    public List<Department> getDepartments(){
        return em.createQuery("select d from Department d", Department.class).getResultList();
    }

    public List<PassingBorderPoint> getPassingBorderPoints(Department department){
        return em.createQuery("select pbp from PassingBorderPoint pbp where pbp.disabled = false " +
                "and pbp.department = :department", PassingBorderPoint.class)
                .setParameter("department", department)
                .getResultList();
    }

    public boolean isProcessing() {
        return processing;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isError() {
        return error;
    }

    public List<SyncMessage> getSyncMessages() {
        return syncMessages;
    }
}
