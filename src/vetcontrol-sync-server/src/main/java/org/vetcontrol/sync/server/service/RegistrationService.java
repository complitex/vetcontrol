package org.vetcontrol.sync.server.service;

import org.vetcontrol.sync.IRegistrationService;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.02.2010 16:12:07
 */

@WebService(name = "Registration", serviceName = "RegistrationService", targetNamespace = "http://sync.vetcontrol.org")

public class RegistrationService implements IRegistrationService{
    @PersistenceContext
    private EntityManager em;

    /**
    @Override    
    public Client processRegistration(Client client) throws RegistrationException {
        if (client == null){
            throw new RegistrationException("Клиент не должен быть NULL");
        }

        //Уникальность MAC адреса
        Long count = em.createQuery("select count(c) from Client c where c.mac = :mac", Long.class)
                .setParameter("mac", client.getMac())
                .getSingleResult();

        if (count > 0){
            throw new RegistrationException("Клиент с MAC адресом: " + client.getMac() + " уже зарегистрирован в системе");
        }

        if (client.getId() != null){
            //Генерация ключа
            String secureKey = DigestUtils.md5Hex(client.getMac());
            client.setSecureKey(secureKey);

            //Сохранение информации о клиенте
            try {
                em.persist(client);
            } catch (Exception e) {
                throw new RegistrationException("Ошибка сохранения клиента", e);
            }
        }
        
        return client;
    }
    **/

    @Override
    public String test(String test) {
        return test + " HELLO";
    }
}
