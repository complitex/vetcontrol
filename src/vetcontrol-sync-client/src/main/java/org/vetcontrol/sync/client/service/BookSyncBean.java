package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class BookSyncBean extends SyncInfo{
    private static final Logger log = LoggerFactory.getLogger(BookSyncBean.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    private boolean initial = false;

    private final Class[] syncBooks = new Class[]{
            StringCulture.class,
            AddressBook.class, ArrestReason.class, CargoMode.class, CargoProducer.class, CargoReceiver.class,
            CargoSender.class, CargoType.class, CountryBook.class, CountryWithBadEpizooticSituation.class,
            CustomsPoint.class, Department.class, Job.class, MovementType.class, PassingBorderPoint.class,
            Prohibition.class, RegisteredProducts.class, Tariff.class, UnitType.class, VehicleType.class,
            CargoModeCargoType.class, CargoModeUnitType.class};

    private final Map<Class, GenericType> genericTypeMap = new HashMap<Class, GenericType>();

    public BookSyncBean() {
        genericTypeMap.put(AddressBook.class, new GenericType<List<AddressBook>>(){});
        genericTypeMap.put(ArrestReason.class, new GenericType<List<ArrestReason>>(){});
        genericTypeMap.put(CargoMode.class, new GenericType<List<CargoMode>>(){});
        genericTypeMap.put(CargoModeCargoType.class, new GenericType<List<CargoModeCargoType>>(){});
        genericTypeMap.put(CargoModeUnitType.class, new GenericType<List<CargoModeUnitType>>(){});
        genericTypeMap.put(CargoProducer.class, new GenericType<List<CargoProducer>>(){});
        genericTypeMap.put(CargoReceiver.class, new GenericType<List<CargoReceiver>>(){});
        genericTypeMap.put(CargoSender.class, new GenericType<List<CargoSender>>(){});
        genericTypeMap.put(CargoType.class, new GenericType<List<CargoType>>(){});
        genericTypeMap.put(CountryBook.class, new GenericType<List<CountryBook>>(){});
        genericTypeMap.put(CountryWithBadEpizooticSituation.class, new GenericType<List<CountryWithBadEpizooticSituation>>(){});
        genericTypeMap.put(CustomsPoint.class, new GenericType<List<CustomsPoint>>(){});
        genericTypeMap.put(Department.class, new GenericType<List<Department>>(){});
        genericTypeMap.put(Job.class, new GenericType<List<Job>>(){});
        genericTypeMap.put(MovementType.class, new GenericType<List<MovementType>>(){});
        genericTypeMap.put(PassingBorderPoint.class, new GenericType<List<PassingBorderPoint>>(){});
        genericTypeMap.put(Prohibition.class, new GenericType<List<Prohibition>>(){});
        genericTypeMap.put(RegisteredProducts.class, new GenericType<List<RegisteredProducts>>(){});
        genericTypeMap.put(StringCulture.class, new GenericType<List<StringCulture>>(){});
        genericTypeMap.put(Tariff.class, new GenericType<List<Tariff>>(){});
        genericTypeMap.put(UnitType.class, new GenericType<List<UnitType>>(){});
        genericTypeMap.put(VehicleType.class, new GenericType<List<VehicleType>>(){});
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process(){
        for (Class book : syncBooks){
            //noinspection unchecked
            processBook(book);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private <T extends IQuery & IUpdated> void processBook(Class<T> bookClass) {
        String secureKey = clientBean.getCurrentSecureKey();
        Date syncUpdated = DateUtil.getCurrentDate();

        log.debug("\n==================== Synchronizing: {} ============================", bookClass.getSimpleName());

        //Количество записей загрузки
        int count = createJSONClient("/book/" + bookClass.getSimpleName() + "/count")
                .post(Count.class, new SyncRequestEntity(secureKey, getUpdated(bookClass))).getCount();
        start(new SyncEvent(count, bookClass));

        int index = 0;
        if (count > 0) {
            @SuppressWarnings({"unchecked"})
            List<T> books = ClientFactory.createJSONClient("/book/" + bookClass.getSimpleName() + "/list")
                    .post((GenericType<List<T>>)genericTypeMap.get(bookClass), new SyncRequestEntity(secureKey, getUpdated(bookClass)));

            //Сохранение в базу данных списка
            index = 0;
            for (T book : books){
                //skip null
                if (isSkip(book)) continue;

                sync(new SyncEvent(count, index++, book));

                book.setUpdated(syncUpdated);

                log.debug("Synchronizing: {}", book.toString());
                if (isPersisted(book)){
                    book.getUpdateQuery(em).executeUpdate();
                }else{
                    book.getInsertQuery(em).executeUpdate();
                }
            }
        }

        complete(new SyncEvent(index, bookClass));
        log.debug("++++++++++++++++++++ Synchronizing Complete: {} +++++++++++++++++++\n", bookClass.getSimpleName());
    }

    private boolean isSkip(Object obj){
         if (obj instanceof ILongId){
            return ((ILongId) obj).getId() == null;
        }else if (obj instanceof IEmbeddedId){
            return ((IEmbeddedId)obj).getId() == null;
        }

        throw new IllegalArgumentException();
    }

    private boolean isPersisted(Object obj){
        Object id = null;

        if (obj instanceof ILongId){
            id = ((ILongId) obj).getId();
        }else if (obj instanceof IEmbeddedId){
            id = ((IEmbeddedId) obj).getId();
        }

        return em.createQuery("select count(*) from " + obj.getClass().getSimpleName() + " b where b.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult() > 0;
    }

    private Date getUpdated(Class<? extends IUpdated> book){
        if (initial){
            return new Date(0);
        }

        Date updated = em.createQuery("select max(e.updated) from " + book.getSimpleName() +" e", Date.class).getSingleResult();

        if (updated == null){
            return new Date(0);
        }

        return updated;
    }
}
