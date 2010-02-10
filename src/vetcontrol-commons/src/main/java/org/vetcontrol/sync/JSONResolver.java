package org.vetcontrol.sync;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.xml.bind.IDResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.02.2010 21:23:07
 */
public class JSONResolver {
    private static Logger log = LoggerFactory.getLogger(JSONResolver.class);

    private final static Class[] cTypes = {
            Client.class, Department.class
    };
    
    private final static Set<Class> types = new HashSet<Class>(Arrays.asList(cTypes));

    private static JSONConfiguration getJSONConfiguration(){
        return JSONConfiguration.natural().build();
    }

    @Provider
    public static class JAXBContextResolver implements ContextResolver<JAXBContext>{
        private final JAXBContext context;

        public JAXBContextResolver() throws Exception {
            this.context = new JSONJAXBContext(JSONResolver.getJSONConfiguration(), cTypes);
        }

        @Override
        public JAXBContext getContext(Class<?> objectType) {
            return (types.contains(objectType)) ? context : null;
        }
    }

    @Provider
    public static class UnmarshallerContextResolver implements ContextResolver<Unmarshaller>{
        private final JAXBContext context;

        public UnmarshallerContextResolver() throws Exception{
            this.context = new JSONJAXBContext(JSONResolver.getJSONConfiguration(), cTypes);
        }

        @Override
        public Unmarshaller getContext(Class<?> aClass) {
            if (types.contains(aClass)){
                try {
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    unmarshaller.setProperty(IDResolver.class.getName(), new EntityIDResolver());
                    return unmarshaller;
                } catch (JAXBException e) {
                    log.error(e.getMessage(), e);
                }
            }

            return null;
        }
    }
}
