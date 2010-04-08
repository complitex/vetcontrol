package org.vetcontrol.document.service;

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
import java.math.BigInteger;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 18:22:06
 */
@Stateless
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
        return documentCargo;
    }

    public void save(DocumentCargo documentCargo) {
        List<Cargo> cargos = documentCargo.getCargos();
        documentCargo.setCargos(null);

        if (documentCargo.getId() != null) {
            List<Cargo> dbCargos = em.createQuery("from Cargo dc where dc.documentCargo = :documentCargo", Cargo.class).setParameter("documentCargo", documentCargo).getResultList();

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

            documentCargo.setUpdated(DateUtil.getCurrentDate());
            documentCargo.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);

            documentCargo = em.merge(documentCargo);

            for (Cargo c : cargos) {
                c.setDocumentCargo(documentCargo);
                c.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
                c.setUpdated(DateUtil.getCurrentDate());

                em.merge(c);
                em.flush();
                em.clear();
            }

            documentCargo.setCargos(cargos);
        } else {
            documentCargo.setClient(clientBean.getCurrentClient());
            documentCargo.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
            documentCargo.setCreator(userProfileBean.getCurrentUser());
            documentCargo.setCreated(DateUtil.getCurrentDate());

            em.persist(documentCargo);
            em.flush();
            documentCargo.setId(getLastInsertId());
            em.clear();

            for (Cargo c : cargos) {
                c.setDocumentCargo(documentCargo);
                c.setSyncStatus(Synchronized.SyncStatus.NOT_SYNCHRONIZED);
                c.setUpdated(DateUtil.getCurrentDate());

                em.persist(c);
                em.flush();
                em.clear();
            }
        }
    }

    private Long getLastInsertId() {
        return ((BigInteger) em.createNativeQuery("select LAST_INSERT_ID()").getSingleResult()).longValue();
    }

    public Long getDocumentCargosSize(DocumentCargoFilter filter) {
        Query query = em.createQuery("select count(distinct dc) from DocumentCargo dc "
                + getJoin(filter, null)
                + getWhere(filter));
        setParameters(filter, query);

        return (Long) query.getSingleResult();
    }

    public List<DocumentCargo> getDocumentCargos(DocumentCargoFilter filter, int first, int count, OrderBy orderBy, boolean asc) {
        String select = "select distinct dc from DocumentCargo dc " + getJoin(filter, orderBy);
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
                    order += "WHEN '" + vehicleType.name() + "' THEN '" + VehicleTypeChoicePanel.getDysplayName(vehicleType) + "' ";
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

    public <T> List<T> getList(Class<T> _class) {
        return em.createQuery("from " + _class.getSimpleName(), _class).getResultList();
    }

    public List<Department> getChildDepartments(Department department) {
        List<Department> list = em.createQuery("select d from Department d where d.parent = :department "
                + "or d.parent.parent = :department", Department.class).setParameter("department", department).getResultList();

        list.add(0, department);

        return list;
    }

    public List<PassingBorderPoint> getPassingBorderPoints(Department department){
        return em.createQuery("select pbp from PassingBorderPoint pbp " +
                "where pbp.department = :department and pbp.disabled = false", PassingBorderPoint.class)
                .setParameter("department", department)
                .getResultList();        
    }

    public ClientEntityId getDocumentCargoId(Long id, Long clientId, Long departmentId) {
        try {
            return new ClientEntityId(id, em.find(Client.class, clientId), em.find(Department.class, departmentId));
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getSenderNames(CountryBook country, String filterName) {
        return em.createQuery("select dc.senderName from DocumentCargo dc "
                + "where dc.senderCountry = :country and dc.senderName like :filterName "
                + "group by dc.senderName order by dc.senderName asc", String.class).setParameter("country", country).setParameter("filterName", "%" + filterName + "%").setMaxResults(10).getResultList();
    }

    public List<String> getReceiverNames(String filterName) {
        return em.createQuery("select dc.receiverName from DocumentCargo dc "
                + "where dc.receiverName like :filterName "
                + "group by dc.receiverName order by dc.receiverName asc", String.class).setParameter("filterName", "%" + filterName + "%").setMaxResults(10).getResultList();
    }

    public List<String> getReceiverAddresses(String filterName) {
        return em.createQuery("select dc.receiverAddress from DocumentCargo dc "
                + "where dc.receiverAddress like :filterName "
                + "group by dc.receiverAddress order by dc.receiverAddress asc", String.class).setParameter("filterName", "%" + filterName + "%").setMaxResults(10).getResultList();
    }

    public String getReceiverAddress(String receiverName) {
        try {
            return em.createQuery("select dc.receiverAddress from DocumentCargo dc "
                    + "where dc.receiverName = :receiverName", String.class).setParameter("receiverName", receiverName).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return "";
        }
    }

    public List<CargoProducer> getCargoProducer(CountryBook country) {
        return em.createQuery("select cp from CargoProducer cp where cp.country = :country and cp.disabled = false", CargoProducer.class).setParameter("country", country).getResultList();
    }
}
