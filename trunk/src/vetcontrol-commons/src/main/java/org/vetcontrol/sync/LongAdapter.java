package org.vetcontrol.sync;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.02.2010 18:57:33
 */

public class LongAdapter extends XmlAdapter<String, Long> {

    public Long unmarshal(String v) {
        return Long.parseLong(v);
    }

    public String marshal(Long v) {
        return v.toString();
    }
}

