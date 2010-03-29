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
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Column(name = "version")
    private String version;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Query getInsertQuery(EntityManager em){
       return em.createNativeQuery("insert into client (id, department_id, ip, mac, secure_key, created, updated, sync_status, version)" +
               " value (:id, :department_id, :ip, :mac, :secure_key, :created, :updated, :syncStatus, :version)")
               .setParameter("id", id)
               .setParameter("department_id", department.getId())
               .setParameter("ip", ip)
               .setParameter("mac", mac)
               .setParameter("secure_key", secureKey)
               .setParameter("created", created)
               .setParameter("updated", updated)
               .setParameter("syncStatus", syncStatus.name())
               .setParameter("version", version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        if (!super.equals(o)) return false;

        Client client = (Client) o;

        if (created != null ? !created.equals(client.created) : client.created != null) return false;
        if (department != null ? !department.equals(client.department) : client.department != null) return false;
        if (id != null ? !id.equals(client.id) : client.id != null) return false;
        if (ip != null ? !ip.equals(client.ip) : client.ip != null) return false;
        if (mac != null ? !mac.equals(client.mac) : client.mac != null) return false;
        if (secureKey != null ? !secureKey.equals(client.secureKey) : client.secureKey != null) return false;
        if (updated != null ? !updated.equals(client.updated) : client.updated != null) return false;
        if (version != null ? !version.equals(client.version) : client.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        result = 31 * result + (secureKey != null ? secureKey.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
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
                + ", updated: " + updated
                + ", version: " + version +"]";
    }
}
