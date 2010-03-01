package org.vetcontrol.entity;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.02.2010 18:23:13
 */
public interface IQuery {
    public Query getInsertQuery(EntityManager em);

    public Query getUpdateQuery(EntityManager em);
}
