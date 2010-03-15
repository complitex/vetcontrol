package org.vetcontrol.client;

import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.embedded.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.03.2010 13:23:45
 */
public class VetcontrolClient {

    private static final String LOG_DIRECTORY = "logs";
    private static final Logger log = Logger.getLogger(VetcontrolClient.class.getName());

    public static void main(String... args) {
        try {
            initLogging();
            Server.Builder serverBuilder = new Server.Builder("VetcontrolClient");

            EmbeddedFileSystem.Builder efsb = new EmbeddedFileSystem.Builder();
            efsb.installRoot(new File("bin\\glassfish"), true);
            File instanceRoot = new File("domain");
            efsb.instanceRoot(instanceRoot);


            serverBuilder.embeddedFileSystem(efsb.build());

            Server server = serverBuilder.build();

            server.createPort(8888);

            server.addContainer(ContainerBuilder.Type.all);

            server.start();

            String test = "jdbc:mysql:mxj://localhost:13306/vetcontrol_client?"
                    + "server.basedir=bin\\mysql"
                    + "&server.datadir=data"
                    + "&createDatabaseIfNotExist=true"
                    + "&server.initialize-user=true"
                    + "&server.default-character-set=utf8"
                    + "&server.innodb_additional_mem_pool_size=4M"
                    + "&server.innodb_flush_log_at_trx_commit=1"
                    + "&server.innodb_lock_wait_timeout=180"
                    + "&server.innodb_log_buffer_size=2M"
                    + "&server.innodb_buffer_pool_size=92M"
                    + "&server.innodb_thread_concurrency=8";
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                log.throwing(VetcontrolClient.class.getName(), "main", e);
            }
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(test, "vc_client_user", "vc_client_pass");

                ResultSet rs = conn.createStatement().executeQuery("select now()");

                while (rs.next()) {
                    log.info("=================== " + rs.getString(1));
                }

                boolean create = true;

                try {
                    rs = conn.createStatement().executeQuery("select count(*) from client");

                    rs.next();

                    create = rs.getInt(1) == 0;
                } catch (SQLException e) {
                    log.throwing(VetcontrolClient.class.getName(), "main", e);
                }

                if (create) {
                    log.info("create tables");
                    importSQL(conn, new FileInputStream("client\\sql\\create.sql"));

                    log.info("insert data");
                    importSQL(conn, new FileInputStream("client\\sql\\insert.sql"));

                    log.info("test data");
                    importSQL(conn, new FileInputStream("client\\sql\\testdump.sql"));
                }
            } catch (SQLException e) {
                log.throwing(VetcontrolClient.class.getName(), "main", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    log.throwing(VetcontrolClient.class.getName(), "main", e);
                }
            }

            String url = "jdbc\\:mysql\\:mxj\\://localhost\\:13306/vetcontrol_client";

            {
                String command = "create-jdbc-connection-pool";
                ParameterMap params = new ParameterMap();
                params.add("datasourceclassname", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
                params.add("restype", "javax.sql.DataSource");
                params.add("property", "user=vc_client_user:password=vc_client_pass:" + "url=" + url);
                params.add("DEFAULT", "vetcontrol-pool-client");
                CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
                ActionReport report = server.getHabitat().getComponent(ActionReport.class);
                runner.getCommandInvocation(command, report).parameters(params).execute();
            }

            {
                String command = "create-jdbc-resource";
                ParameterMap params = new ParameterMap();
                params.add("connectionpoolid", "vetcontrol-pool-client");
                params.add("jndi_name", "jdbc/vetcontrol_clientdb");
                CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
                ActionReport report = server.getHabitat().getComponent(ActionReport.class);
                runner.getCommandInvocation(command, report).parameters(params).execute();
            }


            {
                String command = "create-auth-realm";
                ParameterMap params = new ParameterMap();
                params.add("classname", "com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm");
                params.add("property", "jaas-context=jdbcRealm:datasource-jndi=jdbc/vetcontrol_clientdb:"
                        + "user-table=user:user-name-column=login:password-column=_password:"
                        + "group-table=usergroup:group-name-column=usergroup:charset=UTF-8");
                params.add("DEFAULT", "VetcontrolRealm");
                CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
                ActionReport report = server.getHabitat().getComponent(ActionReport.class);
                runner.getCommandInvocation(command, report).parameters(params).execute();
            }

            final EmbeddedDeployer deployer = server.getDeployer();
            DeployCommandParameters deployParams = new DeployCommandParameters();
            File archive = new File("client").getAbsoluteFile();
            log.info("Deployed: " + deployer.deploy(archive, deployParams));
        } catch (IOException e) {
            log.throwing(VetcontrolClient.class.getName(), "main", e);
        } catch (LifecycleException e) {
            log.throwing(VetcontrolClient.class.getName(), "main", e);
        }
    }

    public static void importSQL(Connection conn, InputStream in) throws SQLException {
        Scanner s = new Scanner(in, "UTF-8");
        s.useDelimiter("(;(\r)?\n)|(--\n)");
        Statement st = null;
        try {
            st = conn.createStatement();
            while (s.hasNext()) {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/")) {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }

                if (line.trim().length() > 0) {
                    st.execute(line);
                }
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    private static void initLogging() {
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }
}