package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 15:30:55
 */
@Entity
@Table(name = "client_update_item")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateItem implements ILongId{
    public static enum PACKAGING {WAR, JAR, SQL, SQL_ZIP}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "update_id")
    @XmlIDREF
    private Update update;

    private String name;

    private Date created;

    @Enumerated(EnumType.STRING)
    private PACKAGING packaging;

    @Column(name = "check_sum", length = 64)
    private String checkSum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public PACKAGING getPackaging() {
        return packaging;
    }

    public void setPackaging(PACKAGING packaging) {
        this.packaging = packaging;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
