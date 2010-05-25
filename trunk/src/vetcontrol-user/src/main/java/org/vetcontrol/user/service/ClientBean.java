package org.vetcontrol.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.util.DateUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.05.2010 14:01:41
 */
@Stateless(name = "user/ClientBean")
public class ClientBean {
     private static final Logger log = LoggerFactory.getLogger(ClientBean.class);

    @PersistenceContext
    private EntityManager em;

    public enum OrderBy {ID, DEPARTMENT, PASSING_BORDER_POINT, IP, MAC, CREATED, LAST_SYNC, VERSION}

    public List<Client> getClients(ClientFilter filter, int first, int count, OrderBy sort, boolean asc) {
        String select = "select c from Client c"
                + (OrderBy.DEPARTMENT.equals(sort) ? " left join c.department.namesMap as m_department" : "")
                + getWhere(filter);       
        String dir = asc ? "asc" : "desc";

        switch (sort) {
            case ID:
                select += " order by c.id " + dir;
                break;
            case DEPARTMENT:
                select += " order by m_department " + dir;
                break;
            case PASSING_BORDER_POINT:
                select += " order by c.passingBorderPoint.name " + dir;
                break;
            case IP:
                select += " order by c.ip " + dir;
                break;
            case MAC:
                select += " order by c.mac " + dir;
                break;
            case CREATED:
                select += " order by c.created " + dir;
                break;
            case LAST_SYNC:
                select += " order by c.lastSync " + dir;
                break;
            case VERSION:
                select += " order by c.version " + dir;
                break;
        }
        
        TypedQuery<Client> query = em.createQuery(select, Client.class)
                .setFirstResult(first)
                .setMaxResults(count);

        addParameters(filter, query);

        return query.getResultList();
    }

    public Long getClientsCount(ClientFilter filter) {
        String select = "select count(*) from Client c" + getWhere(filter);

        TypedQuery<Long> query = em.createQuery(select, Long.class);
        addParameters(filter, query);

        return query.getSingleResult();
    }

    private String getWhere(ClientFilter filter){
        String where = " where 2+2=4";

        if (filter != null){
            if (filter.getId() != null){
                where += " and c.id like :id";
            }
            if (filter.getDepartment() != null){
                where += " and c.department = :department";
            }
            if (filter.getPassingBorderPoint() != null){
                where += " and c.passingBorderPoint = :passingBorderPoint";
            }
            if (filter.getIp() != null){
                where += " and c.ip like :ip";
            }
            if (filter.getMac() != null){
                where += " and upper(c.mac) like :mac";
            }
            if (filter.getCreated() != null) {
                where += " and c.created between :created_s and :created_e";
            }
            if (filter.getLastSync() != null) {
                where += " and c.lastSync between :lastSync_s and :lastSync_e";
            }
            if (filter.getVersion() != null){
                where += " and c.version like :version";
            }
        }

        return where;
    }

    private void addParameters(ClientFilter filter, Query query){
        if (filter != null){
            if (filter.getId() != null){
                query.setParameter("id", '%' +  filter.getId() + '%');
            }
            if (filter.getDepartment() != null){
                query.setParameter("department", filter.getDepartment());
            }
            if (filter.getPassingBorderPoint() != null){
                query.setParameter("passingBorderPoint", filter.getPassingBorderPoint());
            }
            if (filter.getIp() != null){
                query.setParameter("ip", '%' + filter.getIp() + '%');
            }
            if (filter.getMac() != null){
                query.setParameter("mac", '%' + filter.getMac() + '%');
            }
            if (filter.getCreated() != null) {
                query.setParameter("created_s", filter.getCreated());
                query.setParameter("created_e", DateUtil.getEndOfDay(filter.getCreated()));
            }
            if (filter.getLastSync() != null) {
                query.setParameter("lastSync_s", filter.getLastSync());
                query.setParameter("lastSync_e", DateUtil.getEndOfDay(filter.getLastSync()));
            }
            if (filter.getVersion() != null){
                query.setParameter("version", filter.getVersion());
            }
        }
    }

    public List<Department> getDepartments(){
        return em.createQuery("select d from Department d", Department.class).getResultList();
    }

    public List<PassingBorderPoint> getPassingBorderPoints(Department department){
        if (department == null){
            return em.createQuery("select p from PassingBorderPoint p", PassingBorderPoint.class).getResultList();
        } else{
            return em.createQuery("select p from PassingBorderPoint p where p.department = :department", PassingBorderPoint.class)
                    .setParameter("department", department)
                    .getResultList();
        }          
    }
}
