package org.vetcontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.02.2010 20:35:45
 */
@Singleton(name = "ClientBean")
public class ClientBean {
    private static final Logger log = LoggerFactory.getLogger(ClientBean.class);

    private Client currentClient;

    @PersistenceContext
    private EntityManager em;

    public Client getCurrentClient(){
        if (currentClient == null){
            String mac = getCurrentMAC();
            if (mac != null){
                try {
                    currentClient = em.createQuery("select c from Client c where c.mac = :mac", Client.class)
                            .setParameter("mac", mac)
                            .getSingleResult();
                } catch (NoResultException e) {
                    //client not found
                }
            }
        }

        return currentClient;
    }

    public String getCurrentMAC(){
        try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            String mac = "";
            if (networkInterface.getHardwareAddress() != null){
                for (byte m  : networkInterface.getHardwareAddress()){
                    mac += String.format("%02x-", m);
                }
            }
            if (!mac.isEmpty()){
                mac = mac.substring(0, mac.length()-1).toUpperCase();
                return mac;
            }
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String getCurrentIP(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
