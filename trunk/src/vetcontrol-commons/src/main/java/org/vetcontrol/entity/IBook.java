package org.vetcontrol.entity;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.01.2010 16:51:41
 */
public interface IBook {
    public Integer getId();
    
    public List<StringCulture> getNames();
}
