package org.vetcontrol.document.service;

import org.vetcontrol.entity.*;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.util.DateUtil;
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
                order += "dc.vehicleType";
                break;
            case RECEIVER_NAME:
                order += "dc.cargoReceiver.name";
                break;
            case RECEIVER_ADDRESS:
                order += "dc.cargoReceiver.address";
                break;
            case SENDER_NAME:
                order += "dc.cargoSender.name";
                break;
            case CREATED:
                order += " order by dc.created";
                break;
            case SYNC_STATUS:
                order += " order by dc.syncStatus";
                break;
        }
        order += (asc ? " asc" : " desc");

        TypedQuery<DocumentCargo> query = em.createQuery(select + where + order, DocumentCargo.class);
        setParameters(filter, query);

        return query.setFirstResult(first).setMaxResults(count).getResultList();
    }

    private String getOrderLocaleFilter(String entity) {
        return " order by m_" + entity;
    }

    private String getSelectLocaleFilter(String entity) {
        return " left join dc." + entity + ".namesMap as m_" + entity;
    }

    private String getJoin(DocumentCargoFilter filter, OrderBy orderBy) {
        String join = " ";

        if (OrderBy.MOVEMENT_TYPE.equals(orderBy)) {
            join += getSelectLocaleFilter("movementType");
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

            if (filter.getReceiver().getName() != null) {
                where += " and upper(dc.cargoReceiver.name) like :cargoReceiverName";
            }

            if (filter.getReceiver().getAddress() != null) {
                where += " and upper(dc.cargoReceiver.address) like :cargoReceiverAddress";
            }

            if(filter.getSender().getName() != null){
                where += " and upper(dc.cargoSender.name) like :cargoSenderName";
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
            addParameter(query, "cargoReceiverName", filter.getReceiver().getName());
            addParameter(query, "cargoReceiverAddress", filter.getReceiver().getAddress());
            addParameter(query, "cargoSenderName", filter.getSender().getName());
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

    public ClientEntityId getDocumentCargoId(Long id, Long clientId, Long departmentId) {
        try {
            return new ClientEntityId(id, em.find(Client.class, clientId), em.find(Department.class, departmentId));
        } catch (Exception e) {
            return null;
        }
    }
}
