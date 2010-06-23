package org.vetcontrol.entity;

import org.hibernate.annotations.GenericGenerator;
import org.vetcontrol.sync.adapter.LongAdapter;

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
    @GeneratedValue(generator = "EnhancedIdentityGenerator")
    @GenericGenerator(name = "EnhancedIdentityGenerator", strategy = "org.vetcontrol.hibernate.id.IdentityGenerator")
    @XmlID
    @XmlJavaTypeAdapter(LongAdapter.class)
    private Long id;

    @ManyToOne @JoinColumn(name = "department_id")
    @XmlIDREF
    private Department department;

    @ManyToOne @JoinColumn(name = "passing_border_point_id")
    @XmlIDREF
    private PassingBorderPoint passingBorderPoint;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_sync")
    private Date lastSync;

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

    public PassingBorderPoint getPassingBorderPoint() {
        return passingBorderPoint;
    }

    public void setPassingBorderPoint(PassingBorderPoint passingBorderPoint) {
        this.passingBorderPoint = passingBorderPoint;
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

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
        if (lastSync != null ? !lastSync.equals(client.lastSync) : client.lastSync != null) return false;
        if (mac != null ? !mac.equals(client.mac) : client.mac != null) return false;
        if (passingBorderPoint != null ? !passingBorderPoint.equals(client.passingBorderPoint) : client.passingBorderPoint != null)
            return false;
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
        result = 31 * result + (passingBorderPoint != null ? passingBorderPoint.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        result = 31 * result + (secureKey != null ? secureKey.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (lastSync != null ? lastSync.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[hash: " + Integer.toHexString(hashCode())
                + ", id: " + id
                + ", department: " + department
                + ", passingBorderPoint: " + passingBorderPoint
                + ", ip: " + ip
                + ", mac: " + mac
                + ", secureKey: " + secureKey
                + ", created: " + created
                + ", updated: " + updated
                + ", version: " + version +"]";
    }
}
