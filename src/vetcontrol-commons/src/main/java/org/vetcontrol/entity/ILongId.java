package org.vetcontrol.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.02.2010 20:32:03
 */
public interface ILongId extends Serializable{

    public Long getId();

    public void setId(Long id);
}
