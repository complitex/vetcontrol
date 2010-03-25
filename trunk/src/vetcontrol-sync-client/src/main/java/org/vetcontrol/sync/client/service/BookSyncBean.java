package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import static org.vetcontrol.sync.client.service.ClientFactory.createJSONClient;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.02.2010 15:16:39
 */
@Singleton(name = "BookSyncBean")
@TransactionManagement(TransactionManagementType.BEAN)
public class BookSyncBean extends SyncInfo {

    private static final Logger log = LoggerFactory.getLogger(BookSyncBean.class);
    private static final int NETWORK_BATCH_SIZE = 100;
    private static final int DB_BATCH_SIZE = 3;
    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext
    private EntityManager em;
    private boolean initial = false;
    public final static Class[] syncBooks = new Class[]{
        StringCulture.class,
        AddressBook.class, ArrestReason.class, CargoMode.class, CargoProducer.class, CargoReceiver.class,
        CargoSender.class, CargoType.class, CountryBook.class, CountryWithBadEpizooticSituation.class,
        CustomsPoint.class, Department.class, Job.class, MovementType.class, PassingBorderPoint.class,
        Prohibition.class, RegisteredProducts.class, Tariff.class, UnitType.class, VehicleType.class,
        CargoModeCargoType.class, CargoModeUnitType.class};
    private final Map<Class, GenericType> genericTypeMap = new HashMap<Class, GenericType>();

    public BookSyncBean() {
        genericTypeMap.put(AddressBook.class, new GenericType<List<AddressBook>>() {
        });
        genericTypeMap.put(ArrestReason.class, new GenericType<List<ArrestReason>>() {
        });
        genericTypeMap.put(CargoMode.class, new GenericType<List<CargoMode>>() {
        });
        genericTypeMap.put(CargoModeCargoType.class, new GenericType<List<CargoModeCargoType>>() {
        });
        genericTypeMap.put(CargoModeUnitType.class, new GenericType<List<CargoModeUnitType>>() {
        });
        genericTypeMap.put(CargoProducer.class, new GenericType<List<CargoProducer>>() {
        });
        genericTypeMap.put(CargoReceiver.class, new GenericType<List<CargoReceiver>>() {
        });
        genericTypeMap.put(CargoSender.class, new GenericType<List<CargoSender>>() {
        });
        genericTypeMap.put(CargoType.class, new GenericType<List<CargoType>>() {
        });
        genericTypeMap.put(CountryBook.class, new GenericType<List<CountryBook>>() {
        });
        genericTypeMap.put(CountryWithBadEpizooticSituation.class, new GenericType<List<CountryWithBadEpizooticSituation>>() {
        });
        genericTypeMap.put(CustomsPoint.class, new GenericType<List<CustomsPoint>>() {
        });
        genericTypeMap.put(Department.class, new GenericType<List<Department>>() {
        });
        genericTypeMap.put(Job.class, new GenericType<List<Job>>() {
        });
        genericTypeMap.put(MovementType.class, new GenericType<List<MovementType>>() {
        });
        genericTypeMap.put(PassingBorderPoint.class, new GenericType<List<PassingBorderPoint>>() {
        });
        genericTypeMap.put(Prohibition.class, new GenericType<List<Prohibition>>() {
        });
        genericTypeMap.put(RegisteredProducts.class, new GenericType<List<RegisteredProducts>>() {
        });
        genericTypeMap.put(StringCulture.class, new GenericType<List<StringCulture>>() {
        });
        genericTypeMap.put(Tariff.class, new GenericType<List<Tariff>>() {
        });
        genericTypeMap.put(UnitType.class, new GenericType<List<UnitType>>() {
        });
        genericTypeMap.put(VehicleType.class, new GenericType<List<VehicleType>>() {
        });
    }

    public boolean isInitial() {
        return initial;
    }

    /**
     * Если true то синхронизируются все записи вне зависимости от даты обновления
     * @param initial Синхронизировать все записи
     */
    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    //TODO: unused. Remove?
    public void process() {
        for (Class book : syncBooks) {
            //noinspection unchecked
            processBook(book);
        }
    }

    /**
     * Удаление справочников
     * @param bookClass класс справочника
     * @param <T> тип справочника
     */
    public <T extends IUpdated> void processDeleted(Class<T> bookClass) {
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing Deleted: {} ============================", bookClass.getSimpleName());

        Date localMaxDeletedDate = null;

        if (initial) {
            localMaxDeletedDate = new Date(0);
        } else {
            localMaxDeletedDate = em.createQuery("select max(d.deleted) from DeletedEmbeddedId d where d.id.entity = :entity", Date.class).
                    setParameter("entity", bookClass.getCanonicalName()).getSingleResult();

            if (localMaxDeletedDate == null) {
                localMaxDeletedDate = new Date(0);
            }
        }

        //Количество записей загрузки
        int count = 0;
        try {
            count = createJSONClient("/book/" + bookClass.getSimpleName() + "/deleted/count").
                    post(Count.class, new SyncRequestEntity(secureKey, localMaxDeletedDate)).getCount();
        } catch (Exception e) {
            throw new EJBException(e);
        }
        start(new SyncEvent(count, new DeletedEmbeddedId(new DeletedEmbeddedId.Id(null, bookClass.getCanonicalName()), null)));

        int index = 0;
        Date serverMaxDeletedDate = null;

        try {
            for (int i = 0; i <= count / NETWORK_BATCH_SIZE; ++i) {

                List<DeletedEmbeddedId> ids = null;
                try {
                    ids = ClientFactory.createJSONClient("/book/" + bookClass.getSimpleName()
                            + "/deleted/list/" + i * NETWORK_BATCH_SIZE + "/" + NETWORK_BATCH_SIZE).post(new GenericType<List<DeletedEmbeddedId>>() {
                    }, new SyncRequestEntity(secureKey, localMaxDeletedDate));
                } catch (Exception e) {
                    //TODO: replace exception type.
                    throw new EJBException(e);
                }

                //Сохранение в базу данных списка
                if (ids != null) {
                    try {
                        Date maxDeletedDateInTX = null;
                        int j = 0;
                        for (DeletedEmbeddedId id : ids) {
                            j++;
                            if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
                                maxDeletedDateInTX = serverMaxDeletedDate;
                                userTransaction.begin();
                            }

                            //skip null
                            if (id.getId() == null && id.getId().getId() != null) {
                                continue;
                            }

                            sync(new SyncEvent(count, index++, id));

                            String[] s = id.getId().getId().split(":");

                            if (maxDeletedDateInTX == null || maxDeletedDateInTX.before(id.getDeleted())) {
                                maxDeletedDateInTX = id.getDeleted();
                            }

                            if (bookClass.equals(CargoModeCargoType.class)) {
                                em.createQuery("delete from CargoModeCargoType t "
                                        + "where t.id.cargoModeId = :id1 and t.id.cargoTypeId = :id2").setParameter("id1", Long.parseLong(s[0])).setParameter("id2", Long.parseLong(s[1])).executeUpdate();
                            } else if (bookClass.equals(CargoModeUnitType.class)) {
                                em.createQuery("delete from CargoModeUnitType t "
                                        + "where t.id.cargoModeId = :id1 and t.id.unitTypeId = :id2").setParameter("id1", Long.parseLong(s[0])).setParameter("id2", Long.parseLong(s[1])).executeUpdate();
                            }

                            if (j % DB_BATCH_SIZE == 0) {
                                userTransaction.commit();
                                serverMaxDeletedDate = maxDeletedDateInTX;
                            }
                        }
                        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
                            userTransaction.commit();
                            serverMaxDeletedDate = maxDeletedDateInTX;
                        }
                    } catch (Exception e) {
                        try {
                            userTransaction.rollback();
                        } catch (SystemException rollbackEx) {
                            log.error("Couldn't to rollback transaction.", rollbackEx);
                        }
                        throw new EJBException(e);
                    }
                }
            }
        } finally {
            if (count > 0 && serverMaxDeletedDate != null) {
                try {
                    userTransaction.begin();
                    DeletedEmbeddedId deletedEmbeddedId = new DeletedEmbeddedId();
                    deletedEmbeddedId.setId(new DeletedEmbeddedId.Id("sync", bookClass.getCanonicalName()));
                    deletedEmbeddedId.setDeleted(serverMaxDeletedDate);
                    em.merge(deletedEmbeddedId);
                    userTransaction.commit();
                } catch (Exception exc) {
                    log.error("Couldn't to persist DeletedEmbeddedId entity.", exc);
                    try {
                        userTransaction.rollback();
                    } catch (SystemException rollbackExc) {
                        log.error("Couldn't to rollback transaction.", rollbackExc);
                    }
                }
            }
        }

        complete(new SyncEvent(index, bookClass));
        log.debug("++++++++++++++++++++ Synchronizing Deleted Complete: {} +++++++++++++++++++\n", bookClass.getSimpleName());
    }

    public <T extends IQuery & IUpdated> void processBook(Class<T> bookClass) {
        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: {} ============================", bookClass.getSimpleName());

        Date updated = getUpdated(bookClass);


        //Количество записей загрузки
        int count = 0;
        try {
            count = createJSONClient("/book/" + bookClass.getSimpleName() + "/count").post(Count.class, new SyncRequestEntity(secureKey, updated)).getCount();
        } catch (Exception e) {
            throw new EJBException(e);
        }

        start(new SyncEvent(count, bookClass));

        int index = 0;

        for (int i = 0; i <= count / NETWORK_BATCH_SIZE; ++i) {

            List<T> books = null;
            try {
                books = ClientFactory.createJSONClient("/book/" + bookClass.getSimpleName()
                        + "/list/" + i * NETWORK_BATCH_SIZE + "/" + NETWORK_BATCH_SIZE).post((GenericType<List<T>>) genericTypeMap.get(bookClass), new SyncRequestEntity(secureKey, updated));
            } catch (Exception e) {
                throw new EJBException(e);
            }
            //Сохранение в базу данных списка
            if (books != null) {
                try {
                    int j = 0;
                    for (T book : books) {
                        j++;
                        if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
                            userTransaction.begin();
                        }

                        //skip null
                        if (isSkip(book)) {
                            continue;
                        }

                        sync(new SyncEvent(count, index++, book));

                        log.debug("Synchronizing: {}", book.toString());
                        if (isPersisted(book)) {
                            book.getUpdateQuery(em).executeUpdate();
                        } else {
                            book.getInsertQuery(em).executeUpdate();
                        }

                        if (j % DB_BATCH_SIZE == 0) {
                            userTransaction.commit();
                        }
                    }
                    if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
                        userTransaction.commit();
                    }
                } catch (Exception e) {
                    try {
                        userTransaction.rollback();
                    } catch (SystemException rollbackExc) {
                        log.error("Couldn't to rollback transaction.", rollbackExc);
                    }
                    //TODO: replace transaction type.
                    throw new EJBException(e);
                }
            }
        }

        complete(new SyncEvent(index, bookClass));
        log.debug("++++++++++++++++++++ Synchronizing Complete: {} +++++++++++++++++++\n", bookClass.getSimpleName());
    }

    private boolean isSkip(Object obj) {
        if (obj instanceof ILongId) {
            return ((ILongId) obj).getId() == null;
        } else if (obj instanceof IEmbeddedId) {
            return ((IEmbeddedId) obj).getId() == null;
        }

        throw new IllegalArgumentException();
    }

    private boolean isPersisted(Object obj) {
        Object id = null;

        if (obj instanceof ILongId) {
            id = ((ILongId) obj).getId();
        } else if (obj instanceof IEmbeddedId) {
            id = ((IEmbeddedId) obj).getId();
        }

        return em.createQuery("select count(*) from " + obj.getClass().getSimpleName() + " b where b.id = :id", Long.class).setParameter("id", id).getSingleResult() > 0;
    }

    private Date getUpdated(Class<? extends IUpdated> book) {
        if (initial) {
            return new Date(0);
        }

        Date updated = em.createQuery("select max(e.updated) from " + book.getSimpleName() + " e", Date.class).getSingleResult();

        if (updated == null) {
            return new Date(0);
        }

        return updated;
    }
}
