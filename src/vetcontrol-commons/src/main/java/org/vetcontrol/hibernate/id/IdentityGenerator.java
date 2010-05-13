/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.hibernate.id;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 *
 * @author Artem
 */
public class IdentityGenerator extends org.hibernate.id.IdentityGenerator implements Configurable {

    private static final Logger log = LoggerFactory.getLogger(IdentityGenerator.class);

    private String entityName;

    @Override
    public void configure(Type type, Properties params, Dialect d) throws MappingException {
        entityName = params.getProperty(ENTITY_NAME);
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object object) {
        Serializable identifier = sessionImplementor.getEntityPersister(entityName, object).getIdentifier(object, sessionImplementor);

        log.debug("Class = {} , entity name = {}, identifier object = {}", new Object[]{object.getClass(), entityName, identifier});

        boolean assigned = true;
        if (identifier == null) {
            assigned = false;
        }

        if (assigned) {
            return identifier;
        } else {
            return super.generate(sessionImplementor, object);
        }
    }
}
