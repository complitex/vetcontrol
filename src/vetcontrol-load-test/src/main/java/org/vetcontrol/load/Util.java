/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.CountryBook;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.entity.User;
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
public final class Util {

    public static final String PERSISTENCE_UNIT_NAME = "applicationPersistenceUnitTest";

    private Util() {
    }

    public static Client[] initClients(int clientCount) {
        try {
            Client[] clients = new Client[clientCount];

            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            for (int i = 0; i < clientCount; i++) {
                Client c = new Client();
                c.setCreated(new Date());
                c.setUpdated(new Date());
                c.setDepartment(newDepartment());
                c.setIp("0.0.0.0");
                c.setMac(String.valueOf(i));
                c.setSecureKey(String.valueOf(i));
                HibernateSessionTransformer.getSession(entityManager).saveOrUpdate(c);
                clients[i] = c;
            }

            transaction.commit();
            entityManager.close();
            entityManagerFactory.close();

            return clients;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Department newDepartment() {
        Department department = new Department();
        department.setId(1L);
        return department;
    }

    public static User newCreator() {
        User creator = new User();
        creator.setId(2L);
        return creator;
    }

    public static CountryBook newSenderCountry() {
        CountryBook senderCountry = new CountryBook();
        senderCountry.setId(1L);
        return senderCountry;
    }

    public static CargoMode newCargoMode() {
        CargoMode cargoMode = new CargoMode();
        cargoMode.setId(1L);
        return cargoMode;
    }

    public static PassingBorderPoint newPassingBorderPoint() {
        PassingBorderPoint borderPoint = new PassingBorderPoint();
        borderPoint.setId(1L);
        return borderPoint;
    }
}
