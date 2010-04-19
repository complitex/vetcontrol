package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.sync.client.service.exception.DBOperationException;
import org.vetcontrol.sync.client.service.exception.NetworkConnectionException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final int DB_BATCH_SIZE = 1;
    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;
    @EJB(beanName = "LogBean")
    private LogBean logBean;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext
    private EntityManager em;
    private boolean initial = false;
    public final static Class[] syncBooks = new Class[]{
        StringCulture.class,
        ArrestReason.class, CargoMode.class, CountryBook.class, CargoProducer.class,
        CargoType.class, CountryWithBadEpizooticSituation.class,
        CustomsPoint.class, Department.class, PassingBorderPoint.class, Job.class, MovementType.class,
        RegisteredProducts.class, UnitType.class, CargoModeCargoType.class, CargoModeUnitType.class};
    private final Map<Class, GenericType> genericTypeMap = new HashMap<Class, GenericType>();

    public BookSyncBean() {
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
        genericTypeMap.put(RegisteredProducts.class, new GenericType<List<RegisteredProducts>>() {
        });
        genericTypeMap.put(StringCulture.class, new GenericType<List<StringCulture>>() {
        });
        genericTypeMap.put(UnitType.class, new GenericType<List<UnitType>>() {
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
        Log.EVENT event = Log.EVENT.SYNC_DELETED;
        Log.STATUS lastSyncStatus = logBean.getLastStatus(Log.MODULE.SYNC_CLIENT, event, bookClass);


        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing Deleted: {} ============================", bookClass.getSimpleName());

        Date localMaxDeletedDate = null;
        localMaxDeletedDate = getMaxDeleted(bookClass, localMaxDeletedDate);

        //Количество записей загрузки
        int count = 0;
        try {
            count = createJSONClient("/book/" + bookClass.getSimpleName() + "/deleted/count").
                    post(Count.class, new SyncRequestEntity(secureKey, localMaxDeletedDate, lastSyncStatus)).getCount();
        } catch (Exception e) {
            throw new NetworkConnectionException("Network connection exception.", e, bookClass, event);
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
                    }, new SyncRequestEntity(secureKey, localMaxDeletedDate, lastSyncStatus));
                } catch (Exception e) {
                    throw new NetworkConnectionException("Network connection exception.", e, bookClass, event);
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
                            if (isSkip(id)) {
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
                        } catch (Exception rollbackEx) {
                            log.error("Couldn't to rollback transaction.", rollbackEx);
                        }
                        throw new DBOperationException("Db operation exception.", e, bookClass, event);
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
                } catch (Exception e) {
                    log.error("Couldn't to persist DeletedEmbeddedId entity.", e);
                    try {
                        userTransaction.rollback();
                    } catch (Exception rollbackExc) {
                        log.error("Couldn't to rollback transaction.", rollbackExc);
                    }
                    throw new DBOperationException("Db operation exception.", e, bookClass, event);
                }
            }
        }

        complete(new SyncEvent(index, bookClass, event));
        log.debug("++++++++++++++++++++ Synchronizing Deleted Complete: {} +++++++++++++++++++\n", bookClass.getSimpleName());
    }

    private Date getMaxDeleted(Class bookClass, Date localMaxDeletedDate) {
        if (initial) {
            localMaxDeletedDate = new Date(0);
        } else {
            localMaxDeletedDate = em.createQuery("select max(d.deleted) from DeletedEmbeddedId d where d.id.entity = :entity", Date.class).
                    setParameter("entity", bookClass.getCanonicalName()).
                    getSingleResult();
            if (localMaxDeletedDate == null) {
                localMaxDeletedDate = new Date(0);
            }
        }
        return localMaxDeletedDate;
    }

    public <T extends IQuery & IUpdated> void processBook(Class<T> bookClass) {
        Log.EVENT event = Log.EVENT.SYNC_UPDATED;
        Log.STATUS lastSyncStatus = logBean.getLastStatus(Log.MODULE.SYNC_CLIENT, event, bookClass);

        String secureKey = clientBean.getCurrentSecureKey();

        log.debug("\n==================== Synchronizing: {} ============================", bookClass.getSimpleName());

        Date maxUpdated = getMaxUpdated(bookClass);


        //Количество записей загрузки
        int count = 0;
        try {
            count = createJSONClient("/book/" + bookClass.getSimpleName() + "/count").post(Count.class, new SyncRequestEntity(secureKey, maxUpdated, lastSyncStatus)).getCount();
        } catch (Exception e) {
            throw new NetworkConnectionException("Network connection exception.", e, bookClass, event);
        }

        start(new SyncEvent(count, bookClass));

        int index = 0;

        for (int i = 0; i <= count / NETWORK_BATCH_SIZE; ++i) {

            List<T> books = null;
            try {
                books = ClientFactory.createJSONClient("/book/" + bookClass.getSimpleName()
                        + "/list/" + i * NETWORK_BATCH_SIZE + "/" + NETWORK_BATCH_SIZE)
                        .post((GenericType<List<T>>) genericTypeMap.get(bookClass),
                                new SyncRequestEntity(secureKey, maxUpdated, lastSyncStatus));
            } catch (Exception e) {
                throw new NetworkConnectionException("Network connection exception.", e, bookClass, event);
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
                    } catch (Exception rollbackExc) {
                        log.error("Couldn't to rollback transaction.", rollbackExc);
                    }
                    throw new DBOperationException("Db operation exception.", e, bookClass, event);
                }
            }
        }

        complete(new SyncEvent(index, bookClass, event));
        log.debug("++++++++++++++++++++ Synchronizing Complete: {} +++++++++++++++++++\n", bookClass.getSimpleName());
    }

    private boolean isSkip(Object obj) {
        if (obj instanceof ILongId) {
            return ((ILongId) obj).getId() == null;
        } else if (obj instanceof IEmbeddedId) {
            return ((IEmbeddedId) obj).getId() == null;
        }

        log.error("Unknown entity type.");
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

    private Date getMaxUpdated(Class<? extends IUpdated> book) {
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
