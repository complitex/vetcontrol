package org.vetcontrol.document.service;

import org.apache.wicket.Session;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.web.component.VehicleTypeChoicePanel;
import org.vetcontrol.web.security.SecurityRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.04.2010 19:51:29
 */
@Singleton(name = "ArrestDocumentBean")
@RolesAllowed({SecurityRoles.DOCUMENT_CREATE, SecurityRoles.DOCUMENT_EDIT, SecurityRoles.DOCUMENT_DEP_VIEW})
public class ArrestDocumentBean {
    public enum OrderBy{
        ID, CREATOR, ARREST_DATE, ARREST_REASON, CARGO_TYPE, CARGO_MODE, COUNT, UNIT_TYPE, VEHICLE_TYPE,
        SENDER_COUNTRY, SENDER_NAME, RECEIVER_ADDRESS, RECEIVER_NAME, SYNC_STATUS
    }

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "UserProfileBean")
    private UserProfileBean userProfileBean;

    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    public <T> List<T> getList(Class<T> _class) {
        return em.createQuery("from " + _class.getSimpleName(), _class).getResultList();
    }

    public ArrestDocument loadArrestDocument(ClientEntityId id){
        return em.find(ArrestDocument.class, id);
    }

    public Cargo loadCargo(ClientEntityId id){
        return em.find(Cargo.class, id);                
    }

    public void save(ArrestDocument arrestDocument){
        ArrestDocument saved = em.merge(arrestDocument);

        //set id for ui
        arrestDocument.setId(saved.getId());
    }

    public List<ArrestDocument> getArrestDocuments(ArrestDocumentFilter filter, int first, int count, OrderBy orderBy, boolean asc){
        String select = "select distinct(ad) from ArrestDocument ad ";
        String where = getWhere(filter);
        String join = " ";
        String order = " order by ";

        switch (orderBy){
            case ID:
                order += "ad.department, ad.client, ad.id ";
                break;
            case ARREST_DATE:
                order += "ad.arrestDate";
                break;
            case ARREST_REASON:
                join += getJoinLocaleFilter("arrestReason");
                order += getOrderLocaleFilter("arrestReason");
                break;
            case CARGO_TYPE:
                join += getJoinLocaleFilter("cargoType");
                order += getOrderLocaleFilter("cargoType");
                break;
            case CARGO_MODE:
                join += getJoinLocaleFilter("cargoMode");
                order += getOrderLocaleFilter("cargoMode");
                break;
            case COUNT:
                order += "ad.count";
                break;
            case UNIT_TYPE:
                join += getJoinLocaleFilter("unitType");
                order += getOrderLocaleFilter("unitType");
                break;
            case VEHICLE_TYPE:
                order += " (CASE ad.vehicleType ";
                for (VehicleType vehicleType : VehicleType.values()) {
                    order += "WHEN '" + vehicleType.name() + "' THEN '" + VehicleTypeChoicePanel.getDysplayName(vehicleType, Session.get().getLocale()) + "' ";
                }
                order += "END)";
                break;
            case SENDER_COUNTRY:
                join += getJoinLocaleFilter("senderCountry");
                order += getOrderLocaleFilter("senderCountry");
                break;
            case SENDER_NAME:
                order += "ad.senderName";
                break;
            case RECEIVER_ADDRESS:
                order += "ad.receiverAddress";
                break;
            case RECEIVER_NAME:
                order += "ad.receiverName";
                break;
            case SYNC_STATUS:
                order += "ad.syncStatus";
                break;
        }
        order += (asc ? " asc" : " desc");

        //filter by string key of book entity
        if (filter.getCargoType() != null && !OrderBy.CARGO_TYPE.equals(orderBy)){
            join += getJoinLocaleFilter("cargoType");
        }
        if (filter.getCargoMode() != null && !OrderBy.CARGO_MODE.equals(orderBy)){
            join += getJoinLocaleFilter("cargoMode");
        }

        TypedQuery<ArrestDocument> query = em.createQuery(select + join + where + order, ArrestDocument.class);
        setParameters(filter, query);

        return query.setFirstResult(first).setMaxResults(count).getResultList();
    }

    public Long getArrestDocumentsSize(ArrestDocumentFilter filter){
        String join = "";

        //filter by string key of book entity
        if (filter.getCargoType() != null){
            join += getJoinLocaleFilter("cargoType");
        }
        if (filter.getCargoMode() != null){
            join += getJoinLocaleFilter("cargoMode");
        }

        Query query = em.createQuery("select count(distinct ad) from ArrestDocument ad " + join + getWhere(filter));
        setParameters(filter, query);

        return (Long) query.getSingleResult();
    }

    private String getOrderLocaleFilter(String entity) {
        return " m_" + entity;
    }

    private String getJoinLocaleFilter(String entity) {
        return " left join ad." + entity + ".namesMap as m_" + entity;
    }

    private String getWhere(ArrestDocumentFilter filter){
        String where = "";

        if (filter != null){
            where = " where (2=2) ";

            if (filter.getId() != null) {
                where += " and concat(concat(concat(ad.department.id, '.'), concat(ad.client.id, '.')), ad.id) like :id";
            }
            if (filter.getDepartment() != null) {
                if (filter.isChildDepartments()) {
                    where += " and (ad.department = :department"
                            + " or ad.department.parent = :department "
                            + " or exists(select 1 from Department d where d.parent = :department"
                            + " and d.id = ad.department.parent.id))";
                } else {
                    where += " and ad.department = :department";
                }
            }
            if (filter.getArrestDate() != null){
                where += " and ad.arrestDate between :arrestDate and :arrestDate_end_day";
            }
            if (filter.getArrestReason() != null){
                where += " and ad.arrestReason = :arrestReason";
            }
            if (filter.getCargoType() != null){
                where += " and (ad.cargoType.code like :cargoType or upper(m_cargoType) like :cargoType)";
            }
            if (filter.getCargoMode() != null){
                where += " and upper(m_cargoMode) like :cargoMode";
            }
            if (filter.getCount() != null){
                where += " and concat(ad.count,'') like :count";
            }
            if (filter.getUnitType() != null){
                where += " and ad.unitType = :unitType";
            }
            if (filter.getVehicleType() != null){
                where += " and ad.vehicleType = :vehicleType";
            }
            if (filter.getSenderCountry() != null){
                where += " and ad.senderCountry = :senderCountry";
            }
            if (filter.getSenderName() != null){
                where += " and upper(ad.senderName) like :senderName";
            }
            if (filter.getReceiverAddress() != null){
                where += " and upper(ad.receiverAddress) like :receiverAddress";
            }
            if (filter.getReceiverName() != null){
                where += " and upper(ad.receiverName) like :receiverName";
            }
            if (filter.getSyncStatus() != null) {
                where += " and dc.syncStatus = :syncStatus";
            }
        }

        return where;
    }

    private void setParameters(ArrestDocumentFilter filter, Query query){
        if (filter != null){
            addParameter(query, "id", filter.getId());
            addParameter(query, "department", filter.getDepartment());
            if (filter.getArrestDate() != null){
                query.setParameter("arrestDate", filter.getArrestDate());
                query.setParameter("arrestDate_end_day", DateUtil.getEndOfDay(filter.getArrestDate()));
            }
            addParameter(query, "arrestReason", filter.getArrestReason());
            addParameter(query, "cargoType", filter.getCargoType());
            addParameter(query, "cargoMode", filter.getCargoMode());
            addParameter(query, "count", filter.getCount());
            addParameter(query, "unitType", filter.getUnitType());
            addParameter(query, "vehicleType", filter.getVehicleType());
            addParameter(query, "senderCountry", filter.getSenderCountry());
            addParameter(query, "senderName", filter.getSenderName());
            addParameter(query, "receiverAddress", filter.getReceiverAddress());
            addParameter(query, "receiverName", filter.getReceiverName());
        }
    }

    private void addParameter(Query query, String parameter, Object object) {
        if (object != null) {
            query.setParameter(parameter, object);
        }
    }

    private void addParameter(Query query, String parameter, String s) {
        if (s != null) {
            query.setParameter(parameter, "%" + s.toUpperCase() + "%");
        }
    }
    
     public ClientEntityId getClientEntityId(Long id, Long clientId, Long departmentId) {
        try {
            return new ClientEntityId(id, em.find(Client.class, clientId), em.find(Department.class, departmentId));
        } catch (Exception e) {
            return null;
        }
    }
}
