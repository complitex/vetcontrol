package org.vetcontrol.client;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.03.2010 16:36:18
 */
public class Properties {
    final static String LOG_DIRECTORY = "logs";

    final static int SERVER_PORT = 8888;
    final static int DB_PORT = 13306;

    final static String DB_USER = "vc_client_user";
    final static String DB_PASSWORD = "vc_client_pass";

    final static String DB_BASEDIR = "bin\\mysql";
    final static String DB_DATADIR = "data";

    final static String CLIENT_URL = "http://localhost:" + SERVER_PORT + "/client";

    final static String INIT_DB_URL = "jdbc:mysql:mxj://localhost:" + DB_PORT + "/vetcontrol_client?" +
            "server.basedir=" + DB_BASEDIR +
            "&server.datadir=" + DB_DATADIR +
            "&createDatabaseIfNotExist=true" +
            "&server.initialize-user=true" +
            "&server.default-character-set=utf8" ;

    final static String DB_URL = "jdbc:mysql://localhost:" + DB_PORT + "/vetcontrol_client";

    final static String JDBC_POOL_URL = "jdbc\\:mysql\\:mxj\\://localhost\\:" + DB_PORT + "/vetcontrol_client";


    final static String SERVER_UPDATE_URL = "http://localhost:8080/server/download/";
}
