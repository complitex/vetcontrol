package org.vetcontrol.db.populate;

import org.vetcontrol.db.populate.util.GenerateUtil;
import org.vetcontrol.entity.*;
import org.vetcontrol.information.service.dao.BookDAO;
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.information.service.generator.Sequence;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.Property;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ServerPopulator extends AbstractPopulator {

    /*--------------------------Settings------------------------------------------------------*/
    private static final String SERVER_PERSISTENCE_UNIT_NAME = "populate.server";
    //count of books to generate
    private static final int BOOK_COUNT = 100;
    //count of CargoModeCargoType entries for one CargoMode to generate. The same true for CargoModeUnitType.
    private static final int LINK_TABLE_ROWS = 2;
    /*--------------------------- End settings -----------------------------------------------*/
    private static final Class[] BOOKS = {
        RegisteredProducts.class,
        CountryBook.class,
        VehicleType.class,
        CustomsPoint.class,
        MovementType.class,
        CargoProducer.class,
        UnitType.class,
        CargoType.class,
        Job.class,
        ArrestReason.class,
        PassingBorderPoint.class,
        CountryWithBadEpizooticSituation.class
    };
    private static final String[] SUPPORTED_LOCALES = {"en", "ru"};
    private Sequence sequence;
    private IBookDAO bookDAO;

    public static void main(String[] args) {
        new ServerPopulator().populate();
    }

    @Override
    protected void populate() {

        //cargo mode
        for (int i = 0; i < BOOK_COUNT; i++) {
            startTransaction();
            initServer();
            populateCargoMode();
            endTransaction();
        }

        //remaining books
        for (Class entity : BOOKS) {
            int count = count(entity);
            for (int i = count; i < BOOK_COUNT; i++) {
                startTransaction();
                initServer();
                populate(entity, true);
                endTransaction();
            }
        }
    }

    private void populateCargoMode() {
        CargoMode cargoMode = populate(CargoMode.class, true);

        for (int i = 0; i < LINK_TABLE_ROWS; i++) {
            CargoType ct = populate(CargoType.class, false);
            CargoModeCargoType cmct = new CargoModeCargoType();
            cmct.setId(new CargoModeCargoType.Id(cargoMode.getId(), ct.getId()));
            cmct.setUpdated(GenerateUtil.generateFutureDate());
            getEntityManager().merge(cmct);
        }

        for (int i = 0; i < LINK_TABLE_ROWS; i++) {
            UnitType ut = populate(UnitType.class, true);
            CargoModeUnitType cmut = new CargoModeUnitType();
            cmut.setId(new CargoModeUnitType.Id(cargoMode.getId(), ut.getId()));
            cmut.setUpdated(GenerateUtil.generateFutureDate());
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
            for (Property prop : BeanPropertyUtil.getProperties(entityClass)) {
                if (prop.isLocalizable()) {
                    int length = prop.getLength();
                    List<StringCulture> values = (List<StringCulture>) BeanPropertyUtil.getPropertyValue(bookEntry, prop.getName());
                    for (String locale : SUPPORTED_LOCALES) {
                        StringCulture sc = new StringCulture(new StringCultureId(locale), GenerateUtil.generateString(length));
                        sc.setUpdated(GenerateUtil.generateFutureDate());
                        values.add(sc);
                    }
                } else if (prop.isBookReference()) {
                    Object reference = populate(prop.getType(), true);
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
                            value = GenerateUtil.generateString(prop.getLength());
                        } else if (Date.class.isAssignableFrom(propertyType)) {
                            value = GenerateUtil.generateDate();
                        } else {
                            throw new UnsupportedOperationException("Unhandled type. Class : " + entityClass + ", property type : " + propertyType);
                        }
                        BeanPropertyUtil.setPropertyValue(bookEntry, prop.getName(), value);
                    }
                }
            }
            BeanPropertyUtil.setPropertyValue(bookEntry, BeanPropertyUtil.getVersionPropertyName(), GenerateUtil.generateFutureDate());
            saveBook((Serializable) bookEntry);
            return bookEntry;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveBook(Serializable object) {
        try {
            bookDAO.saveOrUpdate(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
