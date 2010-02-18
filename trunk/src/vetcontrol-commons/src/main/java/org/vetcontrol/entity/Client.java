package org.vetcontrol.entity;

import org.vetcontrol.sync.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.02.2010 15:50:03
 */
@Entity
@Table(name = "client")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Client extends Synchronized implements ILongId{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    @XmlID
    @XmlJavaTypeAdapter(LongAdapter.class)
    private Long id;

    @ManyToOne @JoinColumn(name = "department_id")
    @XmlIDREF
    private Department department;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name ="mac", nullable = false, unique = true)
    private String mac;

    @Column(name = "secure_key", nullable = false, unique = true)
    private String secureKey;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Query getInsertQuery(EntityManager em){
       return em.createNativeQuery("insert into client (id, department_id, ip, mac, secure_key, created, updated, sync_status)" +
               " value (:id, :department_id, :ip, :mac, :secure_key, :created, :updated, :syncStatus)")
               .setParameter("id", id)
               .setParameter("department_id", department.getId())
               .setParameter("ip", ip)
               .setParameter("mac", mac)
               .setParameter("secure_key", secureKey)
               .setParameter("created", created)
               .setParameter("updated", updated)
               .setParameter("syncStatus", syncStatus.name());
    }

    @Override
    public String toString() {
        return "[hash: " + Integer.toHexString(hashCode())
                + ", id: " + id
                + ", department: " + department
                + ", ip: " + ip
                + ", mac: " + mac
                + ", secureKey: " + secureKey
                + ", created: " + created
                + ", updated: " + updated +"]";                
    }
}
