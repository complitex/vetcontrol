package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.NotRegisteredException;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.02.2010 15:16:39
 */
@Stateless(name = "BookSyncBean")
public class BookSyncBean {
    private static final Logger log = LoggerFactory.getLogger(BookSyncBean.class);

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @PersistenceContext
    private EntityManager em;

    private final Class[] syncBooks = new Class[]{
            AddressBook.class, ArrestReason.class, CargoMode.class, CargoProducer.class, CargoReceiver.class,
            CargoSender.class, CargoType.class, CountryBook.class, CountryWithBadEpizooticSituation.class,
            CustomsPoint.class, Department.class, Job.class, MovementType.class, PassingBorderPoint.class,
            Prohibition.class, RegisteredProducts.class, Tariff.class, UnitType.class, VehicleType.class};

    private final Map<Class, GenericType> genericTypeMap = new HashMap<Class, GenericType>();

    public BookSyncBean() {
        genericTypeMap.put(AddressBook.class, new GenericType<List<AddressBook>>(){});
        genericTypeMap.put(ArrestReason.class, new GenericType<List<ArrestReason>>(){});
        genericTypeMap.put(CargoMode.class, new GenericType<List<CargoMode>>(){});
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
        genericTypeMap.put(Tariff.class, new GenericType<List<Tariff>>(){});
        genericTypeMap.put(UnitType.class, new GenericType<List<UnitType>>(){});
        genericTypeMap.put(VehicleType.class, new GenericType<List<VehicleType>>(){});
    }

    public void processStringCulture() throws NotRegisteredException{
        String secureKey = clientBean.getCurrentSecureKey();

        //TODO performance issue

        log.debug("==================== String Culture ===================");
        processStringCulture(secureKey);
        log.debug("++++++++++++++++++++ String Culture: Done +++++++++++++++++++");

    }

    public void processBooks() throws NotRegisteredException{
        String secureKey = clientBean.getCurrentSecureKey();


        for (Class book : syncBooks){
            log.debug("==================== {} ===================", book.getSimpleName());

            //noinspection unchecked
            processBook(secureKey, book);


        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private <T extends ILongId & IQuery & IUpdated> void processBook(String secureKey, Class<T> bookClass){
        @SuppressWarnings({"unchecked"})
        List<T> books = ClientFactory.createJSONClient("/book/" + bookClass.getSimpleName() + "/list")
                .post((GenericType<List<T>>)genericTypeMap.get(bookClass), new SyncRequestEntity(secureKey, getUpdated(bookClass)));

        //Сохранение в базу данных списка пользователей
        for (T book : books){
            //json protocol feature, skip empty entity
            if (book.getId() == null) continue;

            log.debug(book.toString());

            int count = em.createQuery("select count(b) from " + bookClass.getSimpleName() + " b where b.id = :id", Long.class)
                    .setParameter("id", book.getId())
                    .getSingleResult()
                    .intValue();

            book.setUpdated(DateUtil.getCurrentDate());

            if (count != 1){
                book.getInsertQuery(em).executeUpdate();
            }else{                
//                em.merge(book);
            }

            log.debug("saved: " + book.getId());
        }                    
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void processStringCulture(String secureKey){
        List<StringCulture> books = ClientFactory.createJSONClient("/book/StringCulture/list")
                .post(new GenericType<List<StringCulture>>(){}, new SyncRequestEntity(secureKey, getUpdated(StringCulture.class)));

        //Сохранение в базу данных списка пользователей
        for (StringCulture book : books){
            //json protocol feature, skip empty entity
            if (book.getId() == null || book.getId().getId() == null) continue;

            StringCulture sc = em.find(StringCulture.class, book.getId());

            if (sc != null) em.detach(sc);


            book.setUpdated(DateUtil.getCurrentDate());
                        
            if (sc == null){
                book.getInsertQuery(em).executeUpdate();
            }else{
                em.merge(book);
            }

            log.debug(book.toString());
        }
    }

    private Date getUpdated(Class<? extends IUpdated> book){
        return new Date(0);
        
//        Date updated = em.createQuery("select max(e.updated) from " + book.getSimpleName() +" e", Date.class).getSingleResult();
//
//        if (updated == null){
//            return new Date(0);
//        }
//
//        return updated;
    }

}
