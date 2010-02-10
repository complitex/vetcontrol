package org.vetcontrol.sync;

import com.sun.xml.bind.IDResolver;
import org.vetcontrol.entity.ILongId;
import org.xml.sax.SAXException;

import java.util.concurrent.Callable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.02.2010 20:03:05
 */
public class EntityIDResolver extends IDResolver {
    @Override
    public void bind(String id, Object obj) throws SAXException {

    }

    @Override
    public Callable<?> resolve(final String id, final Class targetType) throws SAXException {
        return new Callable(){
            @Override
            public Object call() throws Exception {
                Object obj = targetType.newInstance();

                if (obj instanceof ILongId){
                    ((ILongId)obj).setId(Long.decode(id));
                }
             
                return obj;
            }
        };
    }
}
