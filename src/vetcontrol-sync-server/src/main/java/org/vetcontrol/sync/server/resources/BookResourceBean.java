package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.02.2010 15:17:30
 */
@Stateless @Path("/book")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResourceBean {
    private static final Logger log = LoggerFactory.getLogger(UserResourceBean.class);

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @POST @Path("/ArrestReason/list")
    public GenericEntity<List<ArrestReason>> getArrestReasons(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<ArrestReason>>(getList(ArrestReason.class, requestEntity)){};
    }

    @POST @Path("/AddressBook/list")
    public GenericEntity<List<AddressBook>> getAddressBooks(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<AddressBook>>(getList(AddressBook.class, requestEntity)){};
    }

    @POST @Path("/CargoMode/list")
    public GenericEntity<List<CargoMode>> getCargoModes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoMode>>(getList(CargoMode.class, requestEntity)){};
    }

    @POST @Path("/CargoModeCargoType/list")
    public GenericEntity<List<CargoModeCargoType>> getCargoModeCargoTypes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoModeCargoType>>(getList(CargoModeCargoType.class, requestEntity)){};
    }

    @POST @Path("/CargoModeUnitType/list")
    public GenericEntity<List<CargoModeUnitType>> getCargoModeUnitTypes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoModeUnitType>>(getList(CargoModeUnitType.class, requestEntity)){};
    }

    @POST @Path("/CargoType/list")
    public GenericEntity<List<CargoType>> getCargoTypes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoType>>(getList(CargoType.class, requestEntity)){};
    }

    @POST @Path("/CargoProducer/list")
    public GenericEntity<List<CargoProducer>> getCargoProducers(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoProducer>>(getList(CargoProducer.class, requestEntity)){};
    }

    @POST @Path("/CargoReceiver/list")
    public GenericEntity<List<CargoReceiver>> getCargoReceivers(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoReceiver>>(getList(CargoReceiver.class, requestEntity)){};
    }

    @POST @Path("/CargoSender/list")
    public GenericEntity<List<CargoSender>> getCargoSenders(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CargoSender>>(getList(CargoSender.class, requestEntity)){};
    }

    @POST @Path("/CountryBook/list")
    public GenericEntity<List<CountryBook>> getCountryBooks(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CountryBook>>(getList(CountryBook.class, requestEntity)){};
    }

    @POST @Path("/CountryWithBadEpizooticSituation/list")
    public GenericEntity<List<CountryWithBadEpizooticSituation>> getCountryWithBadEpizooticSituations(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CountryWithBadEpizooticSituation>>(getList(CountryWithBadEpizooticSituation.class, requestEntity)){};
    }

    @POST @Path("/CustomsPoint/list")
    public GenericEntity<List<CustomsPoint>> getCustomsPoints(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<CustomsPoint>>(getList(CustomsPoint.class, requestEntity)){};
    }

    @POST @Path("/Department/list")
    public GenericEntity<List<Department>> getDepartments(SyncRequestEntity requestEntity){
        return new GenericEntity<List<Department>>(getList(Department.class, requestEntity)){};
    }

    @POST @Path("/Job/list")
    public GenericEntity<List<Job>> getJobs(SyncRequestEntity requestEntity){
        return new GenericEntity<List<Job>>(getList(Job.class, requestEntity)){};
    }

    @POST @Path("/MovementType/list")
    public GenericEntity<List<MovementType>> getMovementTypes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<MovementType>>(getList(MovementType.class, requestEntity)){};
    }

    @POST @Path("/PassingBorderPoint/list")
    public GenericEntity<List<PassingBorderPoint>> getPassingBorderPoints(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<PassingBorderPoint>>(getList(PassingBorderPoint.class, requestEntity)){};
    }

    @POST @Path("/Prohibition/list")
    public GenericEntity<List<Prohibition>> getProhibitions(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<Prohibition>>(getList(Prohibition.class, requestEntity)){};
    }

    @POST @Path("/RegisteredProducts/list")
    public GenericEntity<List<RegisteredProducts>> getRegisteredProducts(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<RegisteredProducts>>(getList(RegisteredProducts.class, requestEntity)){};
    }

    @POST @Path("/Tariff/list")
    public GenericEntity<List<Tariff>> getTariffs(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<Tariff>>(getList(Tariff.class, requestEntity)){};
    }

    @POST @Path("/UnitType/list")
    public GenericEntity<List<UnitType>> getUnitTypes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<UnitType>>(getList(UnitType.class, requestEntity)){};
    }

    @POST @Path("/VehicleType/list")
    public GenericEntity<List<VehicleType>> getVehicleTypes(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<VehicleType>>(getList(VehicleType.class, requestEntity)){};
    }

    @POST @Path("/StringCulture/list")
    public GenericEntity<List<StringCulture>> getStringCultures(SyncRequestEntity requestEntity){
        return  new GenericEntity<List<StringCulture>>(getList(StringCulture.class, requestEntity)){};
    }

    private <T> List<T> getList(Class<T> entity, SyncRequestEntity requestEntity){
        try {
            clientBean.getClient(requestEntity.getSecureKey());
        } catch (Exception e) {
            log.error("Неверный ключ регистрации. Доступ запрещен.", e);

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("Неверный ключ регистрации. Доступ запрещен.")
                    .type("text/plain;charset=UTF-8")
                    .build());
        }

        return em.createQuery("select e from "+ entity.getSimpleName() + " e where e.updated >= :updated", entity)
                .setParameter("updated", requestEntity.getUpdated())
                .getResultList();
    }
}
