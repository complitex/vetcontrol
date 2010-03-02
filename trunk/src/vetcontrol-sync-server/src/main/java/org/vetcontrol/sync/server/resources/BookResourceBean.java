package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.02.2010 15:17:30
 */
@Stateless @Path("/book")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResourceBean {
    private static final Logger log = LoggerFactory.getLogger(BookResourceBean.class);
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.vetcontrol.sync.server.resources.ResourceBeans");

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    @EJB(name = "LogBean", beanName = "LogBean")
    private LogBean logBean;
 
    @POST @Path("/ArrestReason/list")
    public GenericEntity<List<ArrestReason>> getArrestReasons(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<ArrestReason>>(getList(ArrestReason.class, re, r)){};
    }

    @POST @Path("/AddressBook/list")
    public GenericEntity<List<AddressBook>> getAddressBooks(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<AddressBook>>(getList(AddressBook.class, re, r)){};
    }

    @POST @Path("/CargoMode/list")
    public GenericEntity<List<CargoMode>> getCargoModes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoMode>>(getList(CargoMode.class, re, r)){};
    }

    @POST @Path("/CargoModeCargoType/list")
    public GenericEntity<List<CargoModeCargoType>> getCargoModeCargoTypes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoModeCargoType>>(getList(CargoModeCargoType.class, re, r)){};
    }

    @POST @Path("/CargoModeUnitType/list")
    public GenericEntity<List<CargoModeUnitType>> getCargoModeUnitTypes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoModeUnitType>>(getList(CargoModeUnitType.class, re, r)){};
    }

    @POST @Path("/CargoType/list")
    public GenericEntity<List<CargoType>> getCargoTypes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoType>>(getList(CargoType.class, re, r)){};
    }

    @POST @Path("/CargoProducer/list")
    public GenericEntity<List<CargoProducer>> getCargoProducers(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoProducer>>(getList(CargoProducer.class, re, r)){};
    }

    @POST @Path("/CargoReceiver/list")
    public GenericEntity<List<CargoReceiver>> getCargoReceivers(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoReceiver>>(getList(CargoReceiver.class, re, r)){};
    }

    @POST @Path("/CargoSender/list")
    public GenericEntity<List<CargoSender>> getCargoSenders(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CargoSender>>(getList(CargoSender.class, re, r)){};
    }

    @POST @Path("/CountryBook/list")
    public GenericEntity<List<CountryBook>> getCountryBooks(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CountryBook>>(getList(CountryBook.class, re, r)){};
    }

    @POST @Path("/CountryWithBadEpizooticSituation/list")
    public GenericEntity<List<CountryWithBadEpizooticSituation>> getCountryWithBadEpizooticSituations(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CountryWithBadEpizooticSituation>>(getList(CountryWithBadEpizooticSituation.class, re, r)){};
    }

    @POST @Path("/CustomsPoint/list")
    public GenericEntity<List<CustomsPoint>> getCustomsPoints(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<CustomsPoint>>(getList(CustomsPoint.class, re, r)){};
    }

    @POST @Path("/Department/list")
    public GenericEntity<List<Department>> getDepartments(SyncRequestEntity re, @Context HttpServletRequest r){
        return new GenericEntity<List<Department>>(getList(Department.class, re, r)){};
    }

    @POST @Path("/Job/list")
    public GenericEntity<List<Job>> getJobs(SyncRequestEntity re, @Context HttpServletRequest r){
        return new GenericEntity<List<Job>>(getList(Job.class, re, r)){};
    }

    @POST @Path("/MovementType/list")
    public GenericEntity<List<MovementType>> getMovementTypes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<MovementType>>(getList(MovementType.class, re, r)){};
    }

    @POST @Path("/PassingBorderPoint/list")
    public GenericEntity<List<PassingBorderPoint>> getPassingBorderPoints(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<PassingBorderPoint>>(getList(PassingBorderPoint.class, re, r)){};
    }

    @POST @Path("/Prohibition/list")
    public GenericEntity<List<Prohibition>> getProhibitions(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<Prohibition>>(getList(Prohibition.class, re, r)){};
    }

    @POST @Path("/RegisteredProducts/list")
    public GenericEntity<List<RegisteredProducts>> getRegisteredProducts(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<RegisteredProducts>>(getList(RegisteredProducts.class, re, r)){};
    }

    @POST @Path("/Tariff/list")
    public GenericEntity<List<Tariff>> getTariffs(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<Tariff>>(getList(Tariff.class, re, r)){};
    }

    @POST @Path("/UnitType/list")
    public GenericEntity<List<UnitType>> getUnitTypes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<UnitType>>(getList(UnitType.class, re, r)){};
    }

    @POST @Path("/VehicleType/list")
    public GenericEntity<List<VehicleType>> getVehicleTypes(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<VehicleType>>(getList(VehicleType.class, re, r)){};
    }

    @POST @Path("/StringCulture/list")
    public GenericEntity<List<StringCulture>> getStringCultures(SyncRequestEntity re, @Context HttpServletRequest r){
        return  new GenericEntity<List<StringCulture>>(getList(StringCulture.class, re, r)){};
    }

    private Client getClient(SyncRequestEntity re, HttpServletRequest r){
        try {
            return clientBean.getClient(re.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, BookResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(rb.getString("error.secure_key.check"))
                    .type("text/plain;charset=UTF-8")
                    .build());
        }
    }

    @POST @Path("/{entity}/count")
    public Count getEntityCount(@PathParam("entity") String entity, SyncRequestEntity re, @Context HttpServletRequest r){
        getClient(re, r);
        return new Count(em.createQuery("select count(*) from "+ entity + " e where e.updated >= :updated", Long.class)
                .setParameter("updated", re.getUpdated()).getSingleResult().intValue());
    }

    private <T> List<T> getList(Class<T> entity, SyncRequestEntity re, HttpServletRequest r){         
        Client client = getClient(re, r);

        List<T> list = em.createQuery("select e from "+ entity.getSimpleName() + " e where e.updated >= :updated", entity)
                .setParameter("updated", re.getUpdated())
                .getResultList();

        if (!list.isEmpty()){
            logBean.info(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, BookResourceBean.class, entity,
                    rb.getString("info.sync.processed"), client.getId(), list.size(), r.getRemoteHost(), client.getIp());

            log.info("Синхронизация " + entity.getSimpleName() + ". " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});
        }

        return list;
    }
}
