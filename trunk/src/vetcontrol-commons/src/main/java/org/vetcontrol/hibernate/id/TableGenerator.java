/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.hibernate.id;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.property.ChainedPropertyAccessor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.PropertyAccessorFactory;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 *
 * @author Artem
 */
public class TableGenerator extends org.hibernate.id.enhanced.TableGenerator {

    public static final String PROPERTY_NAME = "property";

    private String propertyName;

    private static final Logger log = LoggerFactory.getLogger(TableGenerator.class);

    private String entityName;

    @Override
    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        entityName = params.getProperty(ENTITY_NAME);
        propertyName = determineProperty(params);
        if (StringHelper.isEmpty(propertyName)) {
            throw new MappingException("Property parameter was not specified.");
        }
        super.configure(type, params, dialect);
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object object) {
        Class entityClass = object.getClass();
        Serializable identifier = sessionImplementor.getEntityPersister(entityName, object).getIdentifier(object, sessionImplementor);
        log.debug("Class = {}, entity name = {}, identifier object = {}", new Object[]{object.getClass(), entityName, identifier});

        Serializable id = null;

        boolean assigned = true;
        if (identifier == null) {
            assigned = false;
        } else {
            Class idClass = identifier.getClass();
            PropertyAccessor propertyAccessor = new ChainedPropertyAccessor(
                    new PropertyAccessor[]{
                        PropertyAccessorFactory.getPropertyAccessor(idClass, null),
                        PropertyAccessorFactory.getPropertyAccessor("field")
                    });
            Getter getter = propertyAccessor.getGetter(idClass, propertyName);
            Object idAsObject = getter.get(identifier);
            if (idAsObject == null) {
                assigned = false;
            } else {
                if (!(idAsObject instanceof Serializable)) {
                    throw new IdentifierGenerationException("Property [" + propertyName + "] on id class [" + identifier.getClass()
                            + "] have non serializable type. Entity class : [" + entityClass + "]");
                }
                id = (Serializable) idAsObject;
                log.debug("Id = {}", id);
            }
        }

        if (assigned) {
            return id;
        } else {
            return super.generate(sessionImplementor, object);
        }
    }

    protected String determineProperty(Properties params) {
        return params.getProperty(PROPERTY_NAME);
    }
}
