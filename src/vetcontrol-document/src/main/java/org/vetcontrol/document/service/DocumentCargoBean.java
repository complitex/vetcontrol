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
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 18:22:06
 */
@Stateless(name = "DocumentCargoBean")
@RolesAllowed({SecurityRoles.DOCUMENT_CREATE, SecurityRoles.DOCUMENT_EDIT, SecurityRoles.DOCUMENT_DEP_VIEW})
public class DocumentCargoBean {

    public static enum OrderBy {

        ID, MOVEMENT_TYPE, VECHICLE_TYPE, RECEIVER_NAME, RECEIVER_ADDRESS, SENDER_NAME, SENDER_COUNTRY, CREATED, SYNC_STATUS
    }
    @PersistenceContext
    private EntityManager em;
    @EJB(beanName = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(beanName = "ClientBean")
    private ClientBean clientBean;

    public DocumentCargo loadDocumentCargo(ClientEntityId id) {
        DocumentCargo documentCargo = em.find(DocumentCargo.class, id);
        //load collection
        documentCargo.getCargos().size();
        documentCargo.getVehicles().size();

        return documentCargo;
    }

    public void save(DocumentCargo documentCargo) {
        List<Cargo> cargos = documentCargo.getCargos();
        List<Vehicle> vehicles = documentCargo.getVehicles();
        
        documentCargo.setCargos(null);
        documentCargo.setVehicles(null);

        //edit
        if (documentCargo.getId() != null) {
            //remove cargo
            List<Cargo> dbCargos = em.createQuery("from Cargo c where c.documentCargo = :documentCargo", Cargo.class)
                    .setParameter("documentCargo", documentCargo)
                    .getResultList();

            for (Cargo db : dbCargos) {
                boolean remove = true;

                for (Cargo ui : cargos) {
                    if (db.getId().equals(ui.getId())) {
                        remove = false;
                        break;
                    }
                }

                if (remove) {
                    em.remove(db);
                }
            }

            //remove vehicles
            List<Vehicle> dbVehicles = em.createQuery("from Vehicle v where v.documentCargo = :documentCargo", Vehicle.class)
                    .setParameter("documentCargo", documentCargo)
                    .getResultList();

            for (Vehicle db : dbVehicles) {
                boolean remove = true;

                for (Vehicle ui : vehicles) {
                    if (db.getId().equals(ui.getId())) {
                        remove = false;
                        break;
                    }
                }

                if (remove) {
                    em.remove(db);
                }
            }

            //save document cargo
            documentCargo.setUpdated(DateUtil.getCurrentDate());
            documentCargo.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);

            documentCargo = em.merge(documentCargo);

             //save vehicle
            for (Vehicle v : vehicles) {
                v.setDocumentCargo(documentCargo);
                v.setVehicleType(documentCargo.getVehicleType());
                v.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
                v.setUpdated(DateUtil.getCurrentDate());

                if (v.getId() == null){
                    em.persist(v);
                }else{
                    em.merge(v);
                }
            }

            //save cargo
            for (Cargo c : cargos) {
                c.setDocumentCargo(documentCargo);
                c.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
                c.setUpdated(DateUtil.getCurrentDate());

                if (c.getId() == null){
                    em.persist(c);
                }else{
                    em.merge(c);
                }
            }

            documentCargo.setCargos(cargos);
            documentCargo.setVehicles(vehicles);
        }
        // new
        else {
            //set
            documentCargo.setClient(clientBean.getCurrentClient());
            documentCargo.setCreator(userProfileBean.getCurrentUser());
            documentCargo.setCreated(DateUtil.getCurrentDate());
            documentCargo.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);

            //save document
            DocumentCargo saved = em.merge(documentCargo);

            //update object id for ui
            documentCargo.setId(saved.getId());            

            //save vehicle
            for (Vehicle v : vehicles){
                v.setDocumentCargo(saved);
                v.setVehicleType(saved.getVehicleType());
                v.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
                v.setUpdated(DateUtil.getCurrentDate());

                em.persist(v);
            }

            //save cargo
            for (Cargo c : cargos) {
                c.setDocumentCargo(saved);
                c.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
                c.setUpdated(DateUtil.getCurrentDate());

                em.persist(c);
            }
        }
    }

//    private Long getLastInsertId() {
//        return ((BigInteger) em.createNativeQuery("select LAST_INSERT_ID()").getSingleResult()).longValue();
//    }

    public Long getDocumentCargosSize(DocumentCargoFilter filter) {
        Query query = em.createQuery("select count(dc) from DocumentCargo dc "
                + getJoin(filter, null)
                + getWhere(filter));
        setParameters(filter, query);

        return (Long) query.getSingleResult();
    }

    public List<DocumentCargo> getDocumentCargos(DocumentCargoFilter filter, int first, int count, OrderBy orderBy, boolean asc) {
        String select = "select dc from DocumentCargo dc " + getJoin(filter, orderBy);
        String where = getWhere(filter);

        String order = " order by ";
        switch (orderBy) {
            case ID:
                order += "dc.department, dc.client, dc.id ";
                break;
            case MOVEMENT_TYPE:
                order += getOrderLocaleFilter("movementType");
                break;
            case VECHICLE_TYPE:
                order += " (CASE dc.vehicleType ";
                for (VehicleType vehicleType : VehicleType.values()) {
                    order += "WHEN '" + vehicleType.name() + "' THEN '" + VehicleTypeChoicePanel.getDysplayName(vehicleType, Session.get().getLocale()) + "' ";
                }
                order += "END)";
                break;
            case RECEIVER_NAME:
                order += "dc.receiverName";
                break;
            case RECEIVER_ADDRESS:
                order += "dc.receiverAddress";
                break;
            case SENDER_NAME:
                order += "dc.senderName";
                break;
            case SENDER_COUNTRY:
                order += getOrderLocaleFilter("senderCountry");
                break;
            case CREATED:
                order += "dc.created";
                break;
            case SYNC_STATUS:
                order += "dc.syncStatus";
                break;
        }
        order += (asc ? " asc" : " desc");

        TypedQuery<DocumentCargo> query = em.createQuery(select + where + order, DocumentCargo.class);
        setParameters(filter, query);

        return query.setFirstResult(first).setMaxResults(count).getResultList();
    }

    private String getOrderLocaleFilter(String entity) {
        return " m_" + entity;
    }

    private String getSelectLocaleFilter(String entity) {
        return " left join dc." + entity + ".namesMap as m_" + entity;
    }

    private String getJoin(DocumentCargoFilter filter, OrderBy orderBy) {
        String join = " ";

        if (OrderBy.MOVEMENT_TYPE.equals(orderBy)) {
            join += getSelectLocaleFilter("movementType");
        } else if (OrderBy.SENDER_COUNTRY.equals(orderBy)) {
            join += getSelectLocaleFilter("senderCountry");
        }

        return join;
    }

    private String getWhere(DocumentCargoFilter filter) {
        String where = "";
        if (filter != null) {
            where = " where (1=1) ";

            if (filter.getCreator() != null) {
                where += " and dc.creator = :creator";
            }

            if (filter.getDepartment() != null) {
                if (filter.isChildDepartments()) {
                    where += " and (dc.department = :department"
                            + " or dc.department.parent = :department "
                            + " or exists(select 1 from Department d where d.parent = :department"
                            + " and d.id = dc.department.parent.id))";
                } else {
                    where += " and dc.department = :department";
                }
            }

            if (filter.getMovementType() != null) {
                where += " and dc.movementType = :movementType";
            }

            if (filter.getVehicleType() != null) {
                where += " and dc.vehicleType = :vehicleType";
            }

            if (filter.getReceiverName() != null) {
                where += " and upper(dc.receiverName) like :receiverName";
            }

            if (filter.getReceiverAddress() != null) {
                where += " and upper(dc.receiverAddress) like :receiverAddress";
            }

            if (filter.getSenderName() != null) {
                where += " and upper(dc.senderName) like :senderName";
            }

            if (filter.getSenderCountry() != null) {
                where += " and dc.senderCountry  = :senderCountry";
            }

            if (filter.getDetentionDetails() != null) {
                where += " and upper(dc.detentionDetails) like :detentionDetails";
            }

            if (filter.getDetails() != null) {
                where += " and upper(dc.details) like :details";
            }

            if (filter.getCreated() != null) {
                where += " and dc.created between :created and :created_end_day";
            }

            if (filter.getId() != null) {
                where += " and concat(concat(concat(dc.department.id, '.'), concat(dc.client.id, '.')), dc.id) like :id";
            }

            if (filter.getSyncStatus() != null) {
                where += " and dc.syncStatus = :syncStatus";
            }
        }

        return where;
    }

    private void setParameters(DocumentCargoFilter filter, Query query) {
        if (filter != null) {
            addParameter(query, "id", filter.getId());
            addParameter(query, "creator", filter.getCreator());
            addParameter(query, "department", filter.getDepartment());
            addParameter(query, "movementType", filter.getMovementType());
            addParameter(query, "vehicleType", filter.getVehicleType());
            addParameter(query, "receiverName", filter.getReceiverName());
            addParameter(query, "receiverAddress", filter.getReceiverAddress());
            addParameter(query, "senderName", filter.getSenderName());
            addParameter(query, "senderCountry", filter.getSenderCountry());
            addParameter(query, "detentionDetails", filter.getDetentionDetails());
            addParameter(query, "details", filter.getDetails());
            if (filter.getCreated() != null) {
                query.setParameter("created", filter.getCreated());
                query.setParameter("created_end_day", DateUtil.getEndOfDay(filter.getCreated()));
            }
            addParameter(query, "syncStatus", filter.getSyncStatus());
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

   

    public ClientEntityId getDocumentCargoId(Long id, Long clientId, Long departmentId) {
        try {
            return new ClientEntityId(id, em.find(Client.class, clientId), em.find(Department.class, departmentId));
        } catch (Exception e) {
            return null;
        }
    }



    public List<CargoProducer> getCargoProducer(CountryBook country) {
        return em.createQuery("select cp from CargoProducer cp where cp.country = :country and cp.disabled = false", CargoProducer.class).setParameter("country", country).getResultList();
    }

    public boolean validate(Vehicle vehicle){
        if (vehicle == null){
            return false;            
        }

        if (VehicleType.CONTAINER.equals(vehicle.getVehicleType())){
            if (!vehicle.getVehicleDetails().matches("[a-zA-Z]{4}\\d{6}-\\d")){
                return false;
            }

            String name = null;
            try {
                name = em.createQuery("select cv.carrierName from ContainerValidator cv where upper(cv.prefix) = :prefix", String.class)
                        .setParameter("prefix", vehicle.getVehicleDetails().substring(0,4).toUpperCase())
                        .getSingleResult();
            } catch (NoResultException e) {
                //nothing
            }

            vehicle.setName(name);
        }

        return true;
    }

}
