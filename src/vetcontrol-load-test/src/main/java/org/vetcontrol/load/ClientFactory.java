/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.MediaType;
import org.vetcontrol.sync.JSONResolver;

/**
 *
 * @author Artem
 */
public class ClientFactory {

    public static UniformInterface createJSONClient(String path) {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JSONResolver.JAXBContextResolver.class);
        clientConfig.getClasses().add(JSONResolver.UnmarshallerContextResolver.class);

        String syncServerUrl = Settings.getSyncServerUrl();

        Client client = Client.create(clientConfig);
        client.setConnectTimeout(180000);
        client.setReadTimeout(180000);
        WebResource webResource = client.resource(syncServerUrl + path);
//        webResource.addFilter(new LoggingFilter());
        return webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE);

    }
}
