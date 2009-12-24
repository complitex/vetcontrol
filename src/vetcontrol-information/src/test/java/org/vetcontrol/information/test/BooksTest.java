package org.vetcontrol.information.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.Assert;
import org.junit.Test;
import org.vetcontrol.information.model.CountryBook;
import org.vetcontrol.information.model.Registeredproducts;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.model.StringCultureId;
import org.vetcontrol.information.service.dao.BookDAO;
import org.vetcontrol.information.service.generator.Sequence;

/**
 *
 * @author Artem
 */
public class BooksTest {

    private static final String PERSISTENCE_UNIT_NAME = "applicationPersistenceUnitTest";
    private EntityManagerFactory managerFactory;

    public BooksTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
        managerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

//    @Test
    public void simpleSaveTest() throws SQLException {

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        long value = s.next();

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void saveTest() {

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        //1 countries
        CountryBook book = new CountryBook("en");
        book.addName(new StringCulture(new StringCultureId("en"), "england"));
        book.addName(new StringCulture(new StringCultureId("ru"), "england2"));

        //2 registered products
        Registeredproducts r = new Registeredproducts("Vendor", "uk", "abc123", new Date());
        r.addName(new StringCulture(new StringCultureId("en"), "milk"));
        r.addName(new StringCulture(new StringCultureId("uk"), "milk2"));
        r.addClassificator(new StringCulture(new StringCultureId("en"), "class #1"));

        bookDAO.saveOrUpdate(book);
        bookDAO.saveOrUpdate(r);

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void getBookContentTest() {

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        List<CountryBook> books = bookDAO.getBookContent(CountryBook.class, 0, 2);

        Assert.assertEquals(1, books.size());
        CountryBook book = books.get(0);
        Assert.assertEquals(2, book.getNames().size());

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void updateTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        CountryBook book = bookDAO.getBookContent(CountryBook.class, 0, 2).get(0);
        StringCulture culture = book.getNames().get(0);
        culture.setValue("england_new");
        bookDAO.saveOrUpdate(book);

        transaction.commit();
        entityManager.close();
    }

    public void cleanUp() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        entityManager.createNativeQuery("DELETE FROM countrybook").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM registeredproducts").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM stringculture").executeUpdate();

        transaction.commit();
        entityManager.close();
    }

    @Test
    public void allTest() {
        cleanUp();
        saveTest();
        getBookContentTest();
        updateTest();
        getBookContentTest();
    }

//    @Test
    public void getContentTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        CountryBook example;
        List books;
        //test 1
        example = new CountryBook();
        books = bookDAO.getContent(example, 0, 2);
        Assert.assertEquals(2, books.size());

        //test 2
        example = new CountryBook("us");
        books = bookDAO.getContent(example, 0, 2);
        Assert.assertEquals(0, books.size());

        //test 3
        Registeredproducts propduct = new Registeredproducts();
        propduct.addName(new StringCulture(new StringCultureId(), "m"));
        books = bookDAO.getContent(propduct, 0, 2);
        Assert.assertEquals(1, books.size());

        transaction.commit();
        entityManager.close();
    }
}
