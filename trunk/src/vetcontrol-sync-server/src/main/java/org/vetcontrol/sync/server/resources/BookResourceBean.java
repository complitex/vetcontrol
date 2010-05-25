package org.vetcontrol.sync.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.hibernate.util.EntityPersisterUtil;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.Count;
import org.vetcontrol.sync.SyncRequestEntity;
import org.vetcontrol.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
@Stateless
@Path("/book")
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

    @POST
    @Path("/ContainerValidator/list/{firstResult}/{maxResults}")
    public GenericEntity<List<ContainerValidator>> getContainerValidator(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<ContainerValidator>>(getList(ContainerValidator.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/ArrestReason/list/{firstResult}/{maxResults}")
    public GenericEntity<List<ArrestReason>> getArrestReasons(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<ArrestReason>>(getList(ArrestReason.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoMode/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CargoMode>> getCargoModes(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CargoMode>>(getList(CargoMode.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoModeCargoType/deleted/list/{firstResult}/{maxResults}")
    public GenericEntity<List<DeletedEmbeddedId>> getDeletedCargoModeCargoTypes(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<DeletedEmbeddedId>>(getDeleted(CargoModeCargoType.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoModeCargoType/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CargoModeCargoType>> getCargoModeCargoTypes(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CargoModeCargoType>>(getList(CargoModeCargoType.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoModeUnitType/deleted/list/{firstResult}/{maxResults}")
    public GenericEntity<List<DeletedEmbeddedId>> getDeletedCargoModeUnitType(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<DeletedEmbeddedId>>(getDeleted(CargoModeUnitType.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoModeUnitType/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CargoModeUnitType>> getCargoModeUnitTypes(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CargoModeUnitType>>(getList(CargoModeUnitType.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoType/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CargoType>> getCargoTypes(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CargoType>>(getList(CargoType.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CargoProducer/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CargoProducer>> getCargoProducers(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CargoProducer>>(getList(CargoProducer.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CountryBook/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CountryBook>> getCountryBooks(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CountryBook>>(getList(CountryBook.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CountryWithBadEpizooticSituation/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CountryWithBadEpizooticSituation>> getCountryWithBadEpizooticSituations(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CountryWithBadEpizooticSituation>>(
                getList(CountryWithBadEpizooticSituation.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/CustomsPoint/list/{firstResult}/{maxResults}")
    public GenericEntity<List<CustomsPoint>> getCustomsPoints(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<CustomsPoint>>(getList(CustomsPoint.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/Department/list/{firstResult}/{maxResults}")
    public GenericEntity<List<Department>> getDepartments(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<Department>>(getList(Department.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/Job/list/{firstResult}/{maxResults}")
    public GenericEntity<List<Job>> getJobs(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<Job>>(getList(Job.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/PassingBorderPoint/list/{firstResult}/{maxResults}")
    public GenericEntity<List<PassingBorderPoint>> getPassingBorderPoints(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<PassingBorderPoint>>(getList(PassingBorderPoint.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/RegisteredProducts/list/{firstResult}/{maxResults}")
    public GenericEntity<List<RegisteredProducts>> getRegisteredProducts(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<RegisteredProducts>>(getList(RegisteredProducts.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/UnitType/list/{firstResult}/{maxResults}")
    public GenericEntity<List<UnitType>> getUnitTypes(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<UnitType>>(getList(UnitType.class, re, r, firstResult, maxResults)) {
        };
    }

    @POST
    @Path("/StringCulture/list/{firstResult}/{maxResults}")
    public GenericEntity<List<StringCulture>> getStringCultures(
            SyncRequestEntity re,
            @Context HttpServletRequest r,
            @PathParam("maxResults") String maxResults,
            @PathParam("firstResult") String firstResult) {
        return new GenericEntity<List<StringCulture>>(getList(StringCulture.class, re, r, firstResult, maxResults)) {
        };
    }

    private String getEqualSymbol(Log.STATUS lastSyncStatus) {
        String equalSymbol = "";
        if (lastSyncStatus == null || lastSyncStatus.equals(Log.STATUS.ERROR)) {
            equalSymbol = "=";
        }
        return equalSymbol;
    }

    private Client getClient(SyncRequestEntity re, HttpServletRequest r) {
        try {
            return clientBean.getClient(re.getSecureKey());
        } catch (Exception e) {
            log.error(rb.getString("error.secure_key.check") + "[ip: {}]", r.getRemoteHost());

            logBean.error(Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC, BookResourceBean.class, Client.class,
                    rb.getString("error.secure_key.check") + "[ip: {0}]", r.getRemoteHost());

            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(rb.getString("error.secure_key.check")).type("text/plain;charset=UTF-8").build());
        }
    }

    @POST
    @Path("/{entity}/count")
    public Count getEntityCount(@PathParam("entity") String entity, SyncRequestEntity re, @Context HttpServletRequest r) {
        getClient(re, r);
        String equalSymbol = getEqualSymbol(re.getLastSyncStatus());
        int count = em.createQuery("select count(*) from " + entity + " e where e.updated >" + equalSymbol + " :updated", Long.class).setParameter("updated", re.getUpdated()).getSingleResult().intValue();
        return new Count(count);
    }

    private <T> List<T> getList(Class<T> entity,
            SyncRequestEntity re,
            HttpServletRequest r,
            String firstResult,
            String maxResults) {
        Client client = getClient(re, r);

        String equalSymbol = getEqualSymbol(re.getLastSyncStatus());


        String order = "";
        if (CargoMode.class.equals(entity) || Department.class.equals(entity)) {
            order = ", e.parent.id";
        }

        TypedQuery<T> query = em.createQuery("select e from " + entity.getSimpleName()
                + " e where e.updated >" + equalSymbol + " :updated order by e.updated " + order, entity).setParameter("updated", re.getUpdated());

        if (maxResults != null) {
            query.setMaxResults(Integer.parseInt(maxResults));

        }
        if (firstResult != null) {
            query.setFirstResult(Integer.parseInt(firstResult));
        }

        List<T> list = query.getResultList();

        if (!list.isEmpty()) {
            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_UPDATED, BookResourceBean.class, entity,
                    rb.getString("info.sync.processed"), list.size(), r.getRemoteHost(), client.getIp());

            log.info("Синхронизация " + entity.getSimpleName() + ". " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});

            //Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);
        }

        return list;
    }

    @POST
    @Path("/{entity}/deleted/count")
    public Count getDeletedCount(@PathParam("entity") String entity, SyncRequestEntity re, @Context HttpServletRequest r) {
        getClient(re, r);
        String equalSymbol = getEqualSymbol(re.getLastSyncStatus());

        String entityName = null;

        if (entity.equals(CargoModeCargoType.class.getSimpleName())) {
            entityName = CargoModeCargoType.class.getCanonicalName();
        } else if (entity.equals(CargoModeUnitType.class.getSimpleName())) {
            entityName = CargoModeUnitType.class.getCanonicalName();
        }

        return new Count(em.createQuery("select count(*) from DeletedEmbeddedId d "
                + "where d.deleted >" + equalSymbol + " :deleted and entity = :entity", Long.class).
                setParameter("deleted", re.getUpdated()).
                setParameter("entity", entityName).
                getSingleResult().intValue());
    }

    private List<DeletedEmbeddedId> getDeleted(Class entity,
            SyncRequestEntity re,
            HttpServletRequest r,
            String firstResult,
            String maxResults) {
        Client client = getClient(re, r);
        String equalSymbol = getEqualSymbol(re.getLastSyncStatus());

        TypedQuery<DeletedEmbeddedId> query = em.createQuery("select d from DeletedEmbeddedId d "
                + "where d.deleted >" + equalSymbol + " :deleted and entity = :entity order by d.deleted", DeletedEmbeddedId.class).
                setParameter("deleted", re.getUpdated()).
                setParameter("entity", entity.getCanonicalName());

        if (maxResults != null) {
            query.setMaxResults(Integer.parseInt(maxResults));

        }
        if (firstResult != null) {
            query.setFirstResult(Integer.parseInt(firstResult));
        }

        List<DeletedEmbeddedId> list = query.getResultList();

        if (!list.isEmpty()) {
            logBean.info(client, Log.MODULE.SYNC_SERVER, Log.EVENT.SYNC_DELETED, BookResourceBean.class, entity,
                    rb.getString("info.sync.processed"), list.size(), r.getRemoteHost(), client.getIp());

            log.info("Синхронизация " + entity.getSimpleName() + ". " + rb.getString("info.sync.processed.log"),
                    new Object[]{client.getId(), list.size(), r.getRemoteHost(), client.getIp()});

            //Last Sync
            client.setLastSync(DateUtil.getCurrentDate());
            EntityPersisterUtil.executeUpdate(em, client);
        }

        return list;
    }
}
