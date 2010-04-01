package org.vetcontrol.client;

import com.mysql.management.driverlaunched.ServerLauncherSocketFactory;
import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.embedded.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Scanner;

import static org.vetcontrol.client.Properties.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.03.2010 13:23:45
 */
public class VetcontrolClient{
    private static final Logger log = LoggerFactory.getLogger(VetcontrolClient.class);
    private static Server server;

    private final static File clientDeployDir = new File("client").getAbsoluteFile();
    private final static File clientUpdateDir = new File("update").getAbsoluteFile();

    private static IServer iServer = new IServer(){

        @Override
        public File getClientDeployDir() {
            return clientDeployDir;
        }

        @Override
        public File getClientUpdateDir() {
            if (!clientUpdateDir.exists() && clientUpdateDir.mkdirs()){
                //make dir
            }
            return clientUpdateDir;
        }

        @Override
        public void deployClient() {
            VetcontrolClient.deployClient();
        }

        @Override
        public void undeployClient() {
            VetcontrolClient.undeployClient();
        }
    };

    private static UpdateClient updateClient;

    public static void main(String... args) {
        showSplash();
       
        initLogging();

        messageSplash("Запуск базы данных...");
        initDB();

        messageSplash("Запуск клиента...");
        initServer();

        messageSplash("Запуск приложения...");
        deployClient();

        createTray();

        hideSplash();

        openBrowser();

        updateClient = new UpdateClient(iServer);
    }

    private static void initDB(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(INIT_DB_URL, DB_USER, DB_PASSWORD);

            ResultSet rs = conn.createStatement().executeQuery("select now()");

            while (rs.next()){
                log.info("init database: " + rs.getString(1));
            }

            boolean create = true;

            try {
                rs = conn.createStatement().executeQuery("select count(*) from client");

                rs.next();

                create = rs.getInt(1) == 0;
            } catch (SQLException e) {
                log.debug("data base is empty");
            }

            if (create){
                log.info("init database: execute create.sql");
                importSQL(conn, new FileInputStream("client\\sql\\create.sql"));

                log.info("init database: execute insert.sql");
                importSQL(conn, new FileInputStream("client\\sql\\insert.sql"));

                log.info("init database: execute testdump.sql");
                importSQL(conn, new FileInputStream("client\\sql\\testdump.sql"));
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (FileNotFoundException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private static void initServer(){
        try {
            Server.Builder serverBuilder = new Server.Builder("VetcontrolClient");

            EmbeddedFileSystem.Builder efsb = new EmbeddedFileSystem.Builder();
            efsb.installRoot(new File("bin\\glassfish"), true);
            efsb.instanceRoot(new File("domain"));

            serverBuilder.embeddedFileSystem(efsb.build());

            serverBuilder.logger(true);
            serverBuilder.logFile(new File("log/vetcontrol.log"));

            server = serverBuilder.build();

            server.createPort(SERVER_PORT);

            server.addContainer(ContainerBuilder.Type.all);

            server.start();

            //Connection Pool
            {
                String command = "create-jdbc-connection-pool";
                ParameterMap params = new ParameterMap();
                params.add("datasourceclassname", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
                params.add("restype", "javax.sql.DataSource");
                params.add("property", "user=" + DB_USER + ":password=" + DB_PASSWORD + ":url=" + JDBC_POOL_URL);
                params.add("DEFAULT", "vetcontrol-pool-client");
                CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
                ActionReport report = server.getHabitat().getComponent(ActionReport.class);
                runner.getCommandInvocation(command, report).parameters(params).execute();
            }

            //JDBC Resource
            {
                String command = "create-jdbc-resource";
                ParameterMap params = new ParameterMap();
                params.add("connectionpoolid", "vetcontrol-pool-client");
                params.add("jndi_name", "jdbc/vetcontrol_clientdb");
                CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
                ActionReport report = server.getHabitat().getComponent(ActionReport.class);
                runner.getCommandInvocation(command, report).parameters(params).execute();
            }

            //JDBC Realm
            {
                String command = "create-auth-realm";
                ParameterMap params = new ParameterMap();
                params.add("classname", "com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm");
                params.add("property", "jaas-context=jdbcRealm:datasource-jndi=jdbc/vetcontrol_clientdb:" +
                        "user-table=user:user-name-column=login:password-column=_password:" +
                        "group-table=usergroup:group-name-column=usergroup:charset=UTF-8");
                params.add("DEFAULT", "VetcontrolRealm");
                CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
                ActionReport report = server.getHabitat().getComponent(ActionReport.class);
                runner.getCommandInvocation(command, report).parameters(params).execute();
            }
        } catch (IOException e) {
            log.error("Ошибка запуска сервера", e);
        } catch (LifecycleException e) {
            log.error("Ошибка запуска сервера", e);
        }
    }

    private static void deployClient(){
        final EmbeddedDeployer deployer = server.getDeployer();
        DeployCommandParameters deployParams = new DeployCommandParameters();

        String deployed = deployer.deploy(clientDeployDir, deployParams);

        log.info("Deployed: " + deployed);
    }

    private static void undeployClient(){
        server.getDeployer().undeployAll();
    }

    private static void createTray(){
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(VetcontrolClient.class.getResource("tray.gif"));

        //Open menu item action
        ActionListener openListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBrowser();
            }
        };

        //Update menu item action
        ActionListener updateListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateClient.show();
            }
        };

        //Exit menu item action
        ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    server.getDeployer().undeployAll();
                    server.stop();
                } catch (LifecycleException e1) {
                    log.error(e1.getLocalizedMessage());
                }

                ServerLauncherSocketFactory.shutdown(new File(DB_BASEDIR), new File(DB_DATADIR));

                System.exit(0);
            }
        };

        PopupMenu popup = new PopupMenu();

        MenuItem open = new MenuItem("Открыть");
        open.addActionListener(openListener);
        popup.add(open);

        MenuItem update = new MenuItem("Обновление");
        update.addActionListener(updateListener);
        popup.add(update);

        popup.addSeparator();

        MenuItem exit = new MenuItem("Выход");
        exit.addActionListener(exitListener);
        popup.add(exit);

        final TrayIcon trayIcon = new TrayIcon(image, "Ветеринарный контроль", popup);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBrowser();
            }
        };

        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(actionListener);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            log.error("Ошибка создания иконки в панели задач");
        }
    }

    private static void importSQL(Connection conn, InputStream in) throws SQLException{
        Scanner s = new Scanner(in, "UTF-8");
        s.useDelimiter("(;(\r)?\n)|(--\n)");
        Statement st = null;
        try{
            st = conn.createStatement();
            while (s.hasNext()){
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/")){
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }

                if (line.trim().length() > 0){
                    st.execute(line);
                }
            }
        } finally{
            if (st != null) st.close();
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private static void initLogging() {
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }

    private static JWindow splash = new JWindow();
    private static JProgressBar progressBar = new JProgressBar();

    private static void showSplash(){
        Image image = Toolkit.getDefaultToolkit().getImage(VetcontrolClient.class.getResource("splash.gif"));
        JLabel label = new JLabel(new ImageIcon(image));
        splash.add(label, BorderLayout.CENTER);

        progressBar.setBorder(BorderFactory.createEmptyBorder(16, 10, 16, 10));
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        splash.add(progressBar, BorderLayout.SOUTH);

        splash.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = label.getPreferredSize();

        splash.setLocation((screenSize.width/2) - (labelSize.width/2),(screenSize.height/2) - (labelSize.height/2));

        splash.setVisible(true);
    }

    private static void hideSplash(){
        splash.dispose();
    }

    private static void messageSplash(String m){
        progressBar.setString(m);
    }

    private static void openBrowser(){
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        try {
            desktop.browse(new URI(CLIENT_URL));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
}
