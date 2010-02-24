package org.vetcontrol.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.02.2010 0:07:55
 */
public interface IEmbeddedId<T> extends Serializable{
    public T getId();

    public void setId(T id);
}
