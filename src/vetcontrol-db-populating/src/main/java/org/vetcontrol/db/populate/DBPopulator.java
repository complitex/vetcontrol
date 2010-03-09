package org.vetcontrol.db.populate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import org.vetcontrol.entity.AddressBook;
import org.vetcontrol.entity.ArrestReason;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.CargoModeCargoType;
import org.vetcontrol.entity.CargoModeUnitType;
import org.vetcontrol.entity.CargoProducer;
import org.vetcontrol.entity.CargoReceiver;
import org.vetcontrol.entity.CargoSender;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.CountryBook;
import org.vetcontrol.entity.CustomsPoint;
import org.vetcontrol.entity.Job;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.entity.Prohibition;
import org.vetcontrol.entity.RegisteredProducts;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.entity.StringCultureId;
import org.vetcontrol.entity.Tariff;
import org.vetcontrol.entity.UnitType;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.information.service.dao.BookDAO;
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.Property;

public class DBPopulator {

    private static final String PERSISTENCE_UNIT_NAME = "populate.server";
    private EntityManagerFactory managerFactory;
    private static final Class[] ENTITIES = {
        RegisteredProducts.class,
        Prohibition.class,
        CountryBook.class,
        VehicleType.class,
        CargoReceiver.class,
        CargoSender.class,
        CustomsPoint.class,
        MovementType.class,
        CargoProducer.class,
        UnitType.class,
        CargoType.class,
        Job.class,
        Tariff.class,
        ArrestReason.class,
        AddressBook.class,
        PassingBorderPoint.class
    };
    private final static Random RANDOM = new Random();
    private static final String[] SUPPORTED_LOCALES = {"en", "ru"};
    private static final int COUNT = 100;
    private static final int LINK_TABLE_ROWS = 20;
    private EntityManager entityManager;
    private Sequence sequence;
    private IBookDAO bookDAO;
    private static final char[] SYMBOLS = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx) {
            SYMBOLS[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            SYMBOLS[idx] = (char) ('a' + idx - 10);
        }
    }

    public DBPopulator() {
        managerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public static void main(String[] args) {
        new DBPopulator().populate();
    }

    private void populate() {

        //cargo mode
        for (int i = 0; i < COUNT; i++) {
            startTransaction();
            initBookDAO();
            populateCargoMode();
            endTransaction();
        }

        //books
        for (Class entity : ENTITIES) {
            int count = count(entity);
            for (int i = count; i < COUNT; i++) {
                startTransaction();
                initBookDAO();
                populate(entity);
                endTransaction();
            }
        }
    }

    private int count(Class entity) {
        int count = entityManager.createQuery("SELECT COUNT(DISTINCT e) FROM " + entity.getSimpleName() + " e", Long.class).getSingleResult().intValue();
        return count;
    }

    private void populateCargoMode() {
        CargoMode cargoMode = populate(CargoMode.class);

        for (int i = 0; i < LINK_TABLE_ROWS; i++) {
            CargoType ct = populate(CargoType.class);
            CargoModeCargoType cmct = new CargoModeCargoType();
            cmct.setId(new CargoModeCargoType.Id(cargoMode.getId(), ct.getId()));
            entityManager.merge(cmct);
        }

        for (int i = 0; i < LINK_TABLE_ROWS; i++) {
            UnitType ut = populate(UnitType.class);
            CargoModeUnitType cmut = new CargoModeUnitType();
            cmut.setId(new CargoModeUnitType.Id(cargoMode.getId(), ut.getId()));
            entityManager.merge(cmut);
        }
    }

    private <T> T findAny(Class<T> entityClass) {
        int count = count(entityClass);
        if (count == 0) {
            throw new RuntimeException("Count is equal to 0.");
        }
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).
                setFirstResult(RANDOM.nextInt(count)).
                setMaxResults(1).
                getResultList().get(0);
    }

    private <T> T populate(Class<T> entityClass) {
        try {
            if (count(entityClass) >= COUNT) {
                return findAny(entityClass);
            }

            T bookEntry = entityClass.newInstance();
            for (Property prop : BeanPropertyUtil.getProperties(entityClass)) {
                if (prop.isLocalizable()) {
                    int length = prop.getLength();
                    List<StringCulture> values = (List<StringCulture>) BeanPropertyUtil.getPropertyValue(bookEntry, prop.getName());
                    for (String locale : SUPPORTED_LOCALES) {
                        StringCulture sc = new StringCulture(new StringCultureId(locale), generateString(length));
                        values.add(sc);
                    }
                } else if (prop.isBookReference()) {
                    Object reference = populate(prop.getType());
                    BeanPropertyUtil.setPropertyValue(bookEntry, prop.getName(), reference);
                } else {
                    Class propertyType = prop.getType();
                    boolean isPrimitive = false;
                    for (Class primitive : BeanPropertyUtil.PRIMITIVES) {
                        if (primitive.isAssignableFrom(propertyType)) {
                            isPrimitive = true;
                            break;
                        }
                    }
                    if (isPrimitive) {
                        Object value = null;
                        if (String.class.isAssignableFrom(propertyType)) {
                            value = generateString(prop.getLength());
                        } else if (Date.class.isAssignableFrom(propertyType)) {
                            value = generateDate();
                        } else {
                            throw new UnsupportedOperationException("Unhandled type. Class : " + entityClass + ", property type : " + propertyType);
                        }
                        BeanPropertyUtil.setPropertyValue(bookEntry, prop.getName(), value);
                    }
                }
            }
            saveBook((Serializable) bookEntry);
            return bookEntry;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateString(int length) {
        int min = Math.min(length, 30);
        StringBuilder result = new StringBuilder(min);
        for (int i = 0; i < min; i++) {
            result.append(SYMBOLS[RANDOM.nextInt(SYMBOLS.length)]);
        }
        return result.toString();
    }

    private Date generateDate() {
        Calendar c = Calendar.getInstance();
        c.set(DateUtil.getCurrentYear(), RANDOM.nextInt(12), RANDOM.nextInt(c.getActualMaximum(Calendar.DAY_OF_MONTH)),
                RANDOM.nextInt(24), RANDOM.nextInt(60), RANDOM.nextInt(60));
        return c.getTime();
    }

    private void saveBook(Serializable object) {
        try {
            bookDAO.saveOrUpdate(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startTransaction() {
        entityManager = managerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
    }

    private void initBookDAO() {
        sequence = new Sequence();
        sequence.setEntityManager(entityManager);

        bookDAO = new BookDAO();
        bookDAO.setSequence(sequence);
        bookDAO.setEntityManager(entityManager);
    }

    private void endTransaction() {
        try {
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }
}
