package org.vetcontrol.sync.client.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.sync.JSONResolver;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.02.2010 14:49:27
 */
public class ClientFactory {
    private static final Logger log = LoggerFactory.getLogger(UniformInterface.class);

    /**
     * Интерфейс по умолчанию для запросов к серверу по протоколу  jax-rs json  
     * @param path Относительный путь к <code>java:module/env/syncServerUrl</code>
     * @return Uniform interface для выполнения HTTP запроса
     */
    public static UniformInterface createJSONClient(String path){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JSONResolver.JAXBContextResolver.class);
        clientConfig.getClasses().add(JSONResolver.UnmarshallerContextResolver.class);

        String syncServerUrl = ":)";

        try {
            //FIX inject by @Resource(name = "syncServerUrl") return null
            syncServerUrl = (String) new InitialContext().lookup("java:module/env/syncServerUrl");
        } catch (NamingException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        return Client.create(clientConfig)
                .resource(syncServerUrl + path)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON_TYPE);
    }
}
