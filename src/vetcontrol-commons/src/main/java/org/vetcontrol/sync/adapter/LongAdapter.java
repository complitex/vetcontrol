package org.vetcontrol.sync.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.02.2010 18:57:33
 */

public class LongAdapter extends XmlAdapter<String, Long> {

    @Override
    public Long unmarshal(String v) {
        return Long.parseLong(v);
    }

    @Override
    public String marshal(Long v) {
        return v.toString();
    }
}

