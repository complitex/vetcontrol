package org.vetcontrol.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.Test;
import org.vetcontrol.model.CultureString;
import org.vetcontrol.model.CultureStringId;
import org.vetcontrol.model.CountryBook;

/**
 *
 * @author Artem
 */
public class CountryBookTest {

    public CountryBookTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }



    @Test
    public void saveTest() throws SQLException{

        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("applicationPersistenceUnitTest");

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
            CountryBook countryBook = new CountryBook("ru");
            CultureString cs = new CultureString(new CultureStringId("en"), "russia");
            entityManager.merge(cs);
            countryBook.addName(cs);
//            countryBook.addName(new CultureString(new CultureStringId("ru"), "russia2"));

            entityManager.merge(countryBook);
        transaction.rollback();
        entityManager.close();
        managerFactory.close();
    }

}