package org.vetcontrol.db.populate;

import static org.vetcontrol.db.populate.util.GenerateUtil.*;
import org.vetcontrol.entity.*;
import org.vetcontrol.information.service.dao.BookDAO;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.book.Property;
import static org.vetcontrol.book.BeanPropertyUtil.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.book.BookTypes;

public class ServerPopulator extends AbstractPopulator {

    /*--------------------------Settings------------------------------------------------------*/
    private static final String SERVER_PERSISTENCE_UNIT_NAME = "populate.server";
    //count of books to generate

    private static final int BOOK_COUNT = 50;
    //count of CargoModeCargoType entries for one CargoMode to generate. The same true for CargoModeUnitType.

    private static final int LINK_TABLE_ROWS = 2;
    //count of first level CargoModes

    private static final int ROOT_CARGO_MODE_COUNT = 2;
    /*--------------------------- End settings -----------------------------------------------*/

    private static final Logger log = LoggerFactory.getLogger(ServerPopulator.class);

    private static final List<Class> COMMON_BOOKS = BookTypes.common();

    private static final String[] SUPPORTED_LOCALES = {"en", "ru"};

    private Sequence sequence;

    private BookDAO bookDAO;

    public static void main(String[] args) {
        new ServerPopulator().populate();
    }

    @Override
    protected void populate() {

        //cargo mode
        log.info("Start populate {} entity", CargoMode.class.getSimpleName());
        for (int i = 0; i < ROOT_CARGO_MODE_COUNT; i++) {
            startTransaction();
            initServer();
            CargoMode rootCargoMode = populateRootCargoMode();
            for (int j = 0; j < BOOK_COUNT / ROOT_CARGO_MODE_COUNT; j++) {
                populateCargoMode(rootCargoMode);
            }
            endTransaction();
        }
        log.info("End populate {} entity", CargoMode.class.getSimpleName());

        //remaining books
        for (Class entityClazz : COMMON_BOOKS) {
            log.info("Start populate {} entity", entityClazz.getSimpleName());
            int count = count(entityClazz);
            for (int i = count; i < BOOK_COUNT; i++) {
                startTransaction();
                initServer();
                populate(entityClazz, true);
                endTransaction();
            }
            log.info("End populate {} entity", entityClazz.getSimpleName());
        }
    }

    private CargoMode populateRootCargoMode() {
        //first level
        return populate(CargoMode.class, false);
    }

    private void populateCargoMode(CargoMode root) {
        //second level
        CargoMode cargoMode = populate(CargoMode.class, false);
        cargoMode.setParent(root);

        for (int i = 0; i < LINK_TABLE_ROWS; i++) {
            CargoType ct = populate(CargoType.class, true);
            CargoModeCargoType cmct = new CargoModeCargoType();
            cmct.setId(new CargoModeCargoType.Id(cargoMode.getId(), ct.getId()));
            cmct.setUpdated(generateFutureDate());
            getEntityManager().merge(cmct);
        }

        for (int i = 0; i < LINK_TABLE_ROWS; i++) {
            UnitType ut = populate(UnitType.class, true);
            CargoModeUnitType cmut = new CargoModeUnitType();
            cmut.setId(new CargoModeUnitType.Id(cargoMode.getId(), ut.getId()));
            cmut.setUpdated(generateFutureDate());
            getEntityManager().merge(cmut);
        }
    }

    private <T> T populate(Class<T> entityClass, boolean reuseEntities) {
        try {
            if (reuseEntities) {
                if (count(entityClass) >= BOOK_COUNT) {
                    return findAny(entityClass);
                }
            }

            T bookEntry = entityClass.newInstance();
            for (Property prop : getProperties(entityClass)) {
                if (prop.isLocalizable()) {
                    int length = prop.getLength();
                    List<StringCulture> values = (List<StringCulture>) getPropertyValue(bookEntry, prop.getName());
                    for (String locale : SUPPORTED_LOCALES) {
                        StringCulture sc = new StringCulture(new StringCultureId(locale), generateString(length));
                        sc.setUpdated(generateFutureDate());
                        values.add(sc);
                    }
                } else if (prop.isBookReference()) {
                    if (!entityClass.equals(prop.getType())) {
                        Object reference = populate(prop.getType(), true);
                        setPropertyValue(bookEntry, prop.getName(), reference);
                    }
                } else {
                    Class propertyType = prop.getType();
                    if (isPrimitive(propertyType)) {
                        Object value = null;
                        if (String.class.isAssignableFrom(propertyType)) {
                            value = generateString(prop.getLength());
                        } else if (Date.class.isAssignableFrom(propertyType)) {
                            value = generateDate();
                        } else {
                            throw new UnsupportedOperationException("Unhandled type. Class : " + entityClass + ", property type : " + propertyType);
                        }
                        setPropertyValue(bookEntry, prop.getName(), value);
                    }
                }
            }
            setPropertyValue(bookEntry, getVersionPropertyName(), generateFutureDate());
            saveBook((Serializable) bookEntry);
            return bookEntry;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveBook(Serializable object) {
        bookDAO.saveOrUpdate(object);
    }

    private void initServer() {
        sequence = new Sequence();
        sequence.setEntityManager(getEntityManager());

        bookDAO = new BookDAO();
        bookDAO.setSequence(sequence);
        bookDAO.setEntityManager(getEntityManager());
    }

    @Override
    protected String getPersistenceUnitName() {
        return SERVER_PERSISTENCE_UNIT_NAME;
    }
}
