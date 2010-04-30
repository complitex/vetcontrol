package org.vetcontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.sync.NotRegisteredException;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.02.2010 20:35:45
 */
@Singleton(name = "ClientBean")
public class ClientBean {

    private static final Logger log = LoggerFactory.getLogger(ClientBean.class);
    private static final String SERVER_SECURE_KEY = "b2627dab45b9455e947f9755861aea10";
    private Client currentClient;
    private EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    @EJB(beanName = "LogBean")
    private LogBean logBean;

    public Client getCurrentClient() throws NotRegisteredException {
        if (currentClient == null) {
            //server
            try {
                currentClient =  em.createQuery("select c from Client c where c.secureKey = :secureKey", Client.class)
                        .setParameter("secureKey", SERVER_SECURE_KEY).getSingleResult();

                return currentClient;                
            } catch (Exception e) {
                //nothing
            }

            //client
            String mac = getCurrentMAC();
            if (mac != null) {
                try {
                    currentClient = em.createQuery("select c from Client c where c.mac = :mac", Client.class).setParameter("mac", mac).getSingleResult();
                } catch (NoResultException e) {
                    throw new NotRegisteredException();
                }
            }
        }

        return currentClient;
    }
    
    public boolean isServer(){
        return getCurrentClient() != null && currentClient.getSecureKey().equals(SERVER_SECURE_KEY);
    }

    public String getCurrentSecureKey() throws NotRegisteredException {
        return getCurrentClient().getSecureKey();
    }

    public String getCurrentMAC() {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            String mac = "";
            if (networkInterface.getHardwareAddress() != null) {
                for (byte m : networkInterface.getHardwareAddress()) {
                    mac += String.format("%02x-", m);
                }
            }
            if (!mac.isEmpty()) {
                mac = mac.substring(0, mac.length() - 1).toUpperCase();
                return mac;
            }
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String getCurrentIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public Client getClient(String secureKey) {
        return em.createQuery("select c from Client c where c.secureKey = :secureKey", Client.class).setParameter("secureKey", secureKey).getSingleResult();
    }

    public void saveCurrentLastSync(Date date){
        Client client = getCurrentClient();
        client.setLastSync(date);

        currentClient = em.merge(client);
    }
}
