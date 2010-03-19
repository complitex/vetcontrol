package org.vetcontrol.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 15:30:55
 */
@Entity
@Table(name = "client_update_item")
@XmlRootElement
public class UpdateItem implements ILongId{
    public static enum PACKAGING {WAR, JAR}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "update_id")
    private Update update;

    private String name;

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
}
