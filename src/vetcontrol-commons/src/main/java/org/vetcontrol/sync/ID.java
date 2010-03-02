package org.vetcontrol.sync;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.2010 15:54:04
 */
@XmlRootElement
public class ID{
    private Long id;

    public ID() {
    }

    public ID(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
