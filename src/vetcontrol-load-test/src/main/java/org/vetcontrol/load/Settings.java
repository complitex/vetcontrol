/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class Settings {

    private static final Logger log = LoggerFactory.getLogger(Settings.class);

    private static String syncServerUrl;

    private static int clientCount;

    private static int networkBatch;

    //document cargos related settings
    private static long documentCargoStartId;

    private static long documentCargosPerClient;

    static {
        init();
    }

    private static void init() {
        InputStream inputStream = null;
        try {
            inputStream = Settings.class.getResourceAsStream("settings.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            syncServerUrl = properties.getProperty("sync_server_url");
            clientCount = Integer.valueOf(properties.getProperty("client_count"));
            networkBatch = Integer.valueOf(properties.getProperty("network_batch"));

            documentCargoStartId = Long.valueOf(properties.getProperty("document_cargo_start_id"));
            documentCargosPerClient = Long.valueOf(properties.getProperty("document_cargos_per_client"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("Can't to close input stream.", e);
                }
            }
        }
    }

    public static int getClientCount() {
        return clientCount;
    }

    public static long getDocumentCargoStartId() {
        return documentCargoStartId;
    }

    public static long getDocumentCargosPerClient() {
        return documentCargosPerClient;
    }

    public static int getNetworkBatch() {
        return networkBatch;
    }

    public static String getSyncServerUrl() {
        return syncServerUrl;
    }
}
