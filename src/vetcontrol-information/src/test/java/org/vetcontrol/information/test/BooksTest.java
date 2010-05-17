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
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.book.ShowBooksMode;

/**
 *
 * @author Artem
 */
public class BooksTest {

    private static final String PERSISTENCE_UNIT_NAME = "applicationPersistenceUnitTest";

    private EntityManagerFactory managerFactory;

    public BooksTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        managerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void simpleSaveTest() throws SQLException {

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Sequence s = new Sequence();
        s.setEntityManager(entityManager);
        long naxtValue = s.next();
        System.out.println("Next generator value: " + naxtValue);

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

//        cleanUp();

        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
//
//        Sequence s = new Sequence();
//        s.setEntityManager(entityManager);
//        BookDAO bookDAO = new BookDAO();
//        bookDAO.setSequence(s);
//        bookDAO.setEntityManager(entityManager);
//
//        CountryBook book = new CountryBook("en");
//        book.addName(new StringCulture(new StringCultureId("en"), "england"));
//        book.addName(new StringCulture(new StringCultureId("ru"), "england2"));
//        bookDAO.saveOrUpdate(book);
//
//        CountryBook book2 = new CountryBook("ru");
//        book2.addName(new StringCulture(new StringCultureId("en"), "russian"));
//        book2.addName(new StringCulture(new StringCultureId("ru"), "russian2"));
//        bookDAO.saveOrUpdate(book2);
//
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

        String countQ = "SELECT COUNT(DISTINCT e.id) FROM unit_type e";
        String query = "SELECT DISTINCT {c.*} FROM countrybook c "
                + "ORDER BY (SELECT sc.value FROM stringculture sc WHERE sc.locale='ru' AND sc.id = c.name) ASC";
        CountryBook c = (CountryBook) session.createSQLQuery(query).addEntity("c", CountryBook.class).setMaxResults(1).uniqueResult();
        System.out.println("c.id = " + c.getId());
        Long count = (Long) session.createSQLQuery(countQ).addEntity("e", UnitType.class).uniqueResult();
        System.out.println("count = " + count);

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
    public void generateInsertTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CountryBook book = new CountryBook("AA");
        book.setId(100L);
        book.setName(1L);

        Session session = HibernateSessionTransformer.getSession(entityManager);
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        String entityName = session.getSessionFactory().getClassMetadata(CountryBook.class).getEntityName();
        EntityPersister persister = (EntityPersister) sessionImplementor.getEntityPersister(entityName, book);
        persister.insert(persister.getIdentifier(book, sessionImplementor), persister.getPropertyValues(book, sessionImplementor.getEntityMode()),
                book, sessionImplementor);
        sessionImplementor.getBatcher().executeBatch();

        transaction.commit();
        entityManager.close();

    }

//    @Test
    public void updateTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CountryBook book = new CountryBook("AA");
        book.setId(100L);
        book.setName(2L);

        Session session = HibernateSessionTransformer.getSession(entityManager);
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        String entityName = session.getSessionFactory().getClassMetadata(CountryBook.class).getEntityName();
        EntityPersister persister = (EntityPersister) sessionImplementor.getEntityPersister(entityName, book);
        persister.update(persister.getIdentifier(book, sessionImplementor), persister.getPropertyValues(book, sessionImplementor.getEntityMode()),
                null, false, null, null, book, null, sessionImplementor);
        sessionImplementor.getBatcher().executeBatch();

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void insertUpdateTest2() {
        //insert
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        StringCultureId id = new StringCultureId();
        id.setId(1000000L);
        id.setLocale("ru");
        StringCulture sc = new StringCulture(id, "123");

        Session session = HibernateSessionTransformer.getSession(entityManager);
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        String entityName = session.getSessionFactory().getClassMetadata(StringCulture.class).getEntityName();
        EntityPersister persister = (EntityPersister) sessionImplementor.getEntityPersister(entityName, sc);
        persister.insert(persister.getIdentifier(sc, sessionImplementor), persister.getPropertyValues(sc, sessionImplementor.getEntityMode()),
                sc, sessionImplementor);
        sessionImplementor.getBatcher().executeBatch();

        transaction.commit();
        entityManager.close();

        //update
        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        sc.setValue("1234");

        session = HibernateSessionTransformer.getSession(entityManager);
        sessionImplementor = (SessionImplementor) session;
        persister = (EntityPersister) sessionImplementor.getEntityPersister(entityName, sc);
        persister.update(persister.getIdentifier(sc, sessionImplementor), persister.getPropertyValues(sc, sessionImplementor.getEntityMode()), null,
                false, null, null, sc, null, sessionImplementor);
        sessionImplementor.getBatcher().executeBatch();

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void insertUpdateDocumentCargoTest() {
        //insert
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        DocumentCargo dc = newDocumentCargo(50L);

        Session session = HibernateSessionTransformer.getSession(entityManager);
        SessionImplementor sessionImplementor = (SessionImplementor) session;
        String entityName = session.getSessionFactory().getClassMetadata(DocumentCargo.class).getEntityName();
        EntityPersister persister = (EntityPersister) sessionImplementor.getEntityPersister(entityName, dc);
        persister.insert(persister.getIdentifier(dc, sessionImplementor), persister.getPropertyValues(dc, sessionImplementor.getEntityMode()),
                dc, sessionImplementor);
        sessionImplementor.getBatcher().executeBatch();

        transaction.commit();
        entityManager.close();

        //update
        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        dc.setDetails("details 2");
        CountryBook c = new CountryBook();
        c.setId(2L);
        dc.setSenderCountry(c);

        session = HibernateSessionTransformer.getSession(entityManager);
        sessionImplementor = (SessionImplementor) session;
        persister = (EntityPersister) sessionImplementor.getEntityPersister(entityName, dc);
        persister.update(persister.getIdentifier(dc, sessionImplementor), persister.getPropertyValues(dc, sessionImplementor.getEntityMode()), null,
                false, null, null, dc, null, sessionImplementor);
        sessionImplementor.getBatcher().executeBatch();

        transaction.commit();
        entityManager.close();
    }

    private DocumentCargo newDocumentCargo(Long id) {
        DocumentCargo dc = new DocumentCargo();
        dc.setId(id);
        CargoMode cm = new CargoMode();
        cm.setId(1L);
        dc.setCargoMode(cm);
        Client c = new Client();
        c.setId(1L);
        dc.setClient(c);
        Department d = new Department();
        d.setId(1L);
        dc.setDepartment(d);
        User u = new User();
        u.setId(2L);
        dc.setCreator(u);
        dc.setCreated(new Date());
        dc.setUpdated(new Date());
        dc.setMovementType(MovementType.IMPORT);
        dc.setVehicleType(VehicleType.CAR);
        dc.setSenderName("sender");
        CountryBook cb = new CountryBook();
        cb.setId(1L);
        dc.setSenderCountry(cb);
        dc.setReceiverName("receiver");
        dc.setReceiverAddress("receiver address");
        dc.setDetails("details 1");
        return dc;
    }

//    @Test
    public void customIdentityGeneratorTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CountryBook b = new CountryBook();
        b.setCode("11");
        b.setName(1L);
        b.setUpdated(new Date());
        b.setId(100L);

        CountryBook bb = entityManager.merge(b);
        System.out.println("id = " + bb.getId());

        transaction.commit();
        entityManager.close();

    }

//    @Test
    public void customIdentityGeneratorForCompositeKeysTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        DocumentCargo dc = newDocumentCargo(50L);
        DocumentCargo dc2 = entityManager.merge(dc);
        System.out.println("id = " + dc2.getId());

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void generatedKeysTest() {
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Session s = HibernateSessionTransformer.getSession(entityManager);
        s.doWork(new Work() {

            @Override
            public void execute(Connection conn) throws SQLException {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(
                        "INSERT INTO cargo_receiver (`name`, `address`) "
                        + "values ('1', '2')");

                int autoIncKeyFromApi = -1;
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    autoIncKeyFromApi = rs.getInt(1);
                } else {
//                    throw new RuntimeException("Generated keys doesn't work.");
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;

                System.out.println("Key returned from getGeneratedKeys():"
                        + autoIncKeyFromApi);
            }
        });

        transaction.commit();
        entityManager.close();
    }

//    @Test
    public void testEntityPersisterUtil() {
        //document cargo
        EntityManager entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        DocumentCargo dc = newDocumentCargo(50L);
        EntityPersisterUtil.insert(entityManager, dc);

        EntityPersisterUtil.executeBatch(entityManager);
        transaction.commit();
        entityManager.close();

        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        dc.setDetails("details 2");
        EntityPersisterUtil.update(entityManager, dc);

        EntityPersisterUtil.executeBatch(entityManager);
        transaction.commit();
        entityManager.close();

        //country book
        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        CountryBook book = new CountryBook("AA");
        book.setId(100L);
        book.setName(1L);
        EntityPersisterUtil.insert(entityManager, book);

        EntityPersisterUtil.executeBatch(entityManager);
        transaction.commit();
        entityManager.close();

        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        book.setCode("AB");
        EntityPersisterUtil.update(entityManager, book);

        EntityPersisterUtil.executeBatch(entityManager);
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
