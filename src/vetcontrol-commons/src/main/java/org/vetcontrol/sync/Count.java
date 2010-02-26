package org.vetcontrol.sync;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.02.2010 20:33:07
 */
@XmlRootElement
public class Count {
    private int count;

    public Count() {
    }

    public Count(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
