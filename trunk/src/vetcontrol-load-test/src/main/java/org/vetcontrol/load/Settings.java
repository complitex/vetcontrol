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

    //jersey Client API related settings
    private static int clientTimeout;

    private static boolean useApacheHttpClient;

    private static boolean useLoggingFilter;

    // common settings
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

            clientTimeout = Integer.valueOf(properties.getProperty("jersey_client_timeout"));
            useApacheHttpClient = Boolean.valueOf(properties.getProperty("use_apache_http_client"));
            useLoggingFilter = Boolean.valueOf(properties.getProperty("use_logging_filter"));

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

    public static int getClientTimeout() {
        return clientTimeout;
    }

    public static boolean isUseApacheHttpClient() {
        return useApacheHttpClient;
    }

    public static boolean isUseLoggingFilter() {
        return useLoggingFilter;
    }
}
