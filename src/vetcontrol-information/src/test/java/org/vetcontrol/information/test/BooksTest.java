package org.vetcontrol.information.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.JDBC4Connection;
import com.mysql.jdbc.JDBC4PreparedStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.hibernate.Session;
import org.junit.Assert;
import org.vetcontrol.entity.*;
import org.vetcontrol.information.service.dao.BookDAO;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.service.dao.BookViewDAO;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.hibernate.jdbc.Work;
import org.vetcontrol.util.book.entity.ShowBooksMode;

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
        bookDAO.saveOrUpdate(book);
        transaction.commit();
        entityManager.close();

        //2 registered products
//        entityManager = managerFactory.createEntityManager();
//        transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        s.setEntityManager(entityManager);
//        bookDAO.setEntityManager(entityManager);
//
//        Registeredproducts r = new Registeredproducts("abc123", new Date());
//        r.addName(new StringCulture(new StringCultureId("en"), "milk"));
//        r.addName(new StringCulture(new StringCultureId("uk"), "milk2"));
//        r.addClassificator(new StringCulture(new StringCultureId("en"), "class #1"));
//        CountryBook country = bookDAO.getContent(new CountryBook(), 0, 1).get(0);
//        r.setCountry(country);
//        bookDAO.saveOrUpdate(r);
//
//        transaction.commit();
//        entityManager.close();
    }

////    @Test
//    public void getBookContentTest() {
//
//        EntityManager entityManager = managerFactory.createEntityManager();
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        Sequence s = new Sequence();
//        s.setEntityManager(entityManager);
//        BookDAO bookDAO = new BookDAO();
//        bookDAO.setSequence(s);
//        bookDAO.setEntityManager(entityManager);
//
//        List<CountryBook> books = bookDAO.getContent(new CountryBook(), 0, 2);
//
//        Assert.assertEquals(1, books.size());
//        CountryBook book = books.get(0);
//        Assert.assertEquals(2, book.getNames().size());
//
//        transaction.commit();
//        entityManager.close();
//    }
//
////    @Test
//    public void updateTest() {
//        EntityManager entityManager = managerFactory.createEntityManager();
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        Sequence s = new Sequence();
//        s.setEntityManager(entityManager);
//        BookDAO bookDAO = new BookDAO();
//        bookDAO.setSequence(s);
//        bookDAO.setEntityManager(entityManager);
//
//        CountryBook book = bookDAO.getContent(new CountryBook(), 0, 2).get(0);
//        StringCulture culture = book.getNames().get(0);
//        culture.setValue("england_new");
//        bookDAO.saveOrUpdate(book);
//
//        transaction.commit();
//        entityManager.close();
//    }
//
    public void cleanUp() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        entityManager.createNativeQuery("DELETE FROM registeredproducts").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM countrybook").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM vehicletypes").executeUpdate();
//        entityManager.createNativeQuery("DELETE FROM stringculture").executeUpdate();

        transaction.commit();
        entityManager.close();
    }
//
//    @Test
//    public void allTest() {
//        cleanUp();
//        saveTest();
//        getBookContentTest();
//        updateTest();
//        getBookContentTest();
//    }
//
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
        books = bookDAO.getContent(example, 0, 2, "names", true, new Locale("ru"), ShowBooksMode.ENABLED);
        Assert.assertEquals(2, books.size());

//        //test 2
//        example = new CountryBook("us");
//        books = bookDAO.getContent(example, 0, 2);
//        Assert.assertEquals(0, books.size());
//
//        //test 3
//        Registeredproducts propduct = new Registeredproducts();
//        propduct.addName(new StringCulture(new StringCultureId(), "m"));
//        books = bookDAO.getContent(propduct, 0, 2);
//        Assert.assertEquals(1, books.size());

        transaction.commit();
        entityManager.close();
    }
//
////    @Test
//    public void getBookReferenceTest() {
//        cleanUp();
//        saveTest();
//
//        EntityManager entityManager = managerFactory.createEntityManager();
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        Sequence s = new Sequence();
//        s.setEntityManager(entityManager);
//        BookDAO bookDAO = new BookDAO();
//        bookDAO.setSequence(s);
//        bookDAO.setEntityManager(entityManager);
//
//        CountryBook cb = bookDAO.getContent(new CountryBook(), 0, 1).get(0);
//
//        Registeredproducts r = bookDAO.getContent(new Registeredproducts(), 0, 1).get(0);
//        r.setCountry(cb);
//        bookDAO.saveOrUpdate(r);
//
//        transaction.commit();
//        entityManager.close();
//    }
//    @Test

    public void test() {

        cleanUp();

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        CountryBook book = new CountryBook("en");
        book.addName(new StringCulture(new StringCultureId("en"), "england"));
        book.addName(new StringCulture(new StringCultureId("ru"), "england2"));
        bookDAO.saveOrUpdate(book);

        CountryBook book2 = new CountryBook("ru");
        book2.addName(new StringCulture(new StringCultureId("en"), "russian"));
        book2.addName(new StringCulture(new StringCultureId("ru"), "russian2"));
        bookDAO.saveOrUpdate(book2);

        transaction.commit();
        entityManager.close();

        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        Session session = HibernateSessionTransformer.getSession(entityManager);
//        String query = "SELECT DISTINCT {c.*} FROM cargo_mode c join stringculture sc on c.name = sc.id order by sc.value";
//        String hq = "select distinct a from CargoMode a, StringCulture sc where a.name=sc.id.id order by sc.value";
//        String hq2 = "select a from CargoMode a left join StringCulture sc left join a.cargoType cargoType where cargoType.name = sc.id.id "
//                + "and sc.id.locale='ru' and sc.value like :p order by sc.value";
//
////        List<CargoMode> list = session.createSQLQuery(query).addEntity("c", CargoMode.class).list();
//        List<CargoMode> list = session.createQuery(hq2).setParameter("p", "%1%").list();
//        for (CargoMode b : list) {
//            System.out.println(b.getCargoType().getCode());
//        }

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void usersTest() {

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        IBookViewDAO bookViewDAO = new BookViewDAO();
        bookViewDAO.setEntityManager(entityManager);

        User example = new User();
        Department d = new Department();
        StringCulture sc = new StringCulture();
        sc.setValue("Госу");
        d.setNames(Arrays.asList(sc));
        example.setDepartment(d);

        List<User> list = bookViewDAO.getContent(example, 0, 1, "department", true, Locale.ENGLISH, ShowBooksMode.ENABLED);
        for (User b : list) {
            System.out.println(b.getLogin());
        }

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void saveWithIdTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setSequence(s);
        bookDAO.setEntityManager(entityManager);

        CountryBook book = new CountryBook("sw");
        book.addName(new StringCulture(new StringCultureId("en"), "sweden"));
        book.addName(new StringCulture(new StringCultureId("ru"), "швеция"));
        book.setId(999L);
        bookDAO.saveOrUpdate(book);

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void testPreparedStatement() {

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Session s = HibernateSessionTransformer.getSession(entityManager);
        s.doWork(new Work() {

            @Override
            public void execute(Connection connection) throws SQLException {
                Properties props = new Properties();
                props.put(Driver.PASSWORD_PROPERTY_KEY, "artem");
                props.put(Driver.USER_PROPERTY_KEY, "artem");
                JDBC4Connection c = new JDBC4Connection("localhost", 3306, props, "vetcontrol",
                        "jdbc:mysql://localhost:3306/vetcontrol");

                class JDBC4PreparedStatementTest extends JDBC4PreparedStatement {

                    public JDBC4PreparedStatementTest(ConnectionImpl conn, String sql, String catalog) throws SQLException {
                        super(conn, sql, catalog);
                    }

                    @Override
                    public String asSql() throws SQLException {
                        return super.asSql();
                    }
                }
                JDBC4PreparedStatementTest mysqlps = new JDBC4PreparedStatementTest(c, "SELECT * FROM countrybook where id = ?", null);
                mysqlps.setObject(1, 1L);
                String sql = mysqlps.asSql();
                System.out.println(sql);

                PreparedStatement ps = connection.prepareStatement("SELECT * FROM document_cargo where id = ?");
                System.out.println(ps.getClass());
                ps.setString(1, "1");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getString("id") + " " + rs.getString("updated"));
                }
                rs.close();
                ps.close();

            }
        });

        transaction.commit();
        entityManager.close();

    }

//    @Test
//    public void cloneTest() {
//        EntityManager entityManager = managerFactory.createEntityManager();
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//
//        IBookViewDAO bookViewDAO = new BookViewDAO();
//        bookViewDAO.setEntityManager(entityManager);
//
//        CountryBook b1 = bookViewDAO.getContent(CountryBook.class).get(0);
//        CountryBook b2 = (CountryBook) b1.clone();
//
//        transaction.commit();
//        entityManager.close();
//
//        System.out.println("Country book : " + b1);
//        System.out.println("Country book : " + b2);
//
//    }
}
