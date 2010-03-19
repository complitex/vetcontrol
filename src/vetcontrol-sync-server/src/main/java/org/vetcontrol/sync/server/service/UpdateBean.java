package org.vetcontrol.sync.server.service;

import org.vetcontrol.entity.Update;
import org.vetcontrol.util.DateUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 15:59:43
 */
@Stateless(name = "UpdateBean")
public class UpdateBean {
    public static enum OrderBy{ID, VERSION, TYPE, ACTIVE, CREATED}

    @PersistenceContext
    private EntityManager em;

    public List<Update> getUpdates(UpdateFilter filter, int first, int count, OrderBy sort, boolean asc){
        String select = "select u from Update u" + getWhere(filter);

        String dir = asc ? "asc" : "desc";

        switch (sort){
            case ID:
                select += " order by u.id " + dir;
                break;
            case VERSION:
                select += " order by u.version " + dir;
                break;
            case TYPE:
                select += " order by u.type " + dir;
                break;
            case ACTIVE:
                select += " order by u.active " + dir;
                break;
            case CREATED:
                select += " order by u.created " + dir;
                break;            
        }

        TypedQuery<Update> query = em.createQuery(select, Update.class).setFirstResult(first).setMaxResults(count);

        addParameters(filter, query);

        return query.getResultList();
    }

    public Long getUpdatesCount(UpdateFilter filter){
        String select = "select count(u) from Update u" + getWhere(filter);

        TypedQuery<Long> query = em.createQuery(select, Long.class);
        addParameters(filter, query);

        return query.getSingleResult();
    }

    private String getWhere(UpdateFilter filter){
        String where = " where 2+2=4";

        if (filter != null){
            if (filter.getId() != null){
                where += " and u.id like :id";
            }
            if (filter.getVersion() != null){
                where += " and u.version like :version";
            }
            if (filter.getType() != null){
                where += " and u.type = :type";
            }
            if (filter.getActive() != null){
                where += " and u.active = :active";
            }
            if (filter.getCreated() != null){
                where += " and u.created between :date_s and :date_e";
            }
        }

        return where;
    }

    private void addParameters(UpdateFilter filter, Query query){
        if (filter != null){
           if (filter.getId() != null){
               query.setParameter("id", filter.getId());
            }
            if (filter.getVersion() != null){
                query.setParameter("version", filter.getVersion());
            }
            if (filter.getType() != null){
                query.setParameter("type", filter.getType());
            }
            if (filter.getActive() != null){
                query.setParameter("active", filter.getActive());
            }
            if (filter.getCreated() != null){
                query.setParameter("date_s", filter.getCreated());
                query.setParameter("date_e", DateUtil.getEndOfDay(filter.getCreated()));
            }
        }
    }
}
