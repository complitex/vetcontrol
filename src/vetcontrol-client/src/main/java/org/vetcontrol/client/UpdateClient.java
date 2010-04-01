package org.vetcontrol.client;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.vetcontrol.client.Properties.*;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.03.2010 11:28:24
 *
 * Загрузка файлов обновлений клиента и установка
 */
public class UpdateClient {
    private static final Logger log = LoggerFactory.getLogger(UpdateClient.class);
    private static enum STATUS {PROCESS, ERROR, COMPLETE}

    private JFrame frame;
    private JLabel currentVersionLabel, lastVersionLable;
    private JProgressBar progressBar;
    private File updateDir, clientDir;
    private JButton updateButton, cancelButton;
    private STATUS status = STATUS.COMPLETE;
    private IServer iServer;

    /**
     * Инициализация пользователького интерфейса
     * @param iServer используется для вызова методов развертывания приложения и директорий установки
     */
    public UpdateClient(IServer iServer) {
        this.iServer = iServer;

        updateDir = iServer.getClientUpdateDir();
        clientDir = iServer.getClientDeployDir();

        frame = new JFrame("Обновнение клиента");
        frame.setResizable(false);

        Container container = frame.getContentPane();

        Image iconImage = Toolkit.getDefaultToolkit().getImage(UpdateClient.class.getResource("tray.gif"));
        frame.setIconImage(iconImage);

        frame.setPreferredSize(new Dimension(405, 250));

        //version info
        JPanel versionPanel = new JPanel(new BorderLayout(5, 5));
        versionPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        container.add(versionPanel, BorderLayout.PAGE_START);


        currentVersionLabel = new JLabel();
        versionPanel.add(currentVersionLabel, BorderLayout.PAGE_START);

        lastVersionLable = new JLabel();
        versionPanel.add(lastVersionLable, BorderLayout.PAGE_END);

        //update process info
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setBorder(BorderFactory.createEmptyBorder(40,10,40,10));
        container.add(processPanel, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        processPanel.add(progressBar, BorderLayout.PAGE_START);

        //action panel
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.add(updatePanel, BorderLayout.PAGE_END);

        updateButton = new JButton("Обновить");
        updateButton.setVisible(hasUpdate());
        updateButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                status = STATUS.PROCESS;
                progressBar.setVisible(true);

                update();
            }
        });
        updatePanel.add(updateButton);

        cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                status = STATUS.COMPLETE;
                frame.setVisible(false);
            }
        });
        updatePanel.add(cancelButton);
    }

    /**
     * Отобразить окно обновление клиента
     */
    public void show(){
        currentClientVersion = null;
        currentVersionLabel.setText("Текущая версия клиента: " + getCurrentClientVersion());

        lastClientVersion = null;
        lastVersionLable.setText("Последняя доступная версия: "
                + (getLastClientVersion() != null ? getLastClientVersion() : "Нет новых обновлений"));

        frame.pack();
        frame.setLocationRelativeTo(null); //center a frame onscreen
        frame.setVisible(true);
    }

    /**
     * Закрыть окно обновлений
     */
    public void hide(){
        frame.setVisible(false);
    }

    /**
     * Отобразить окно информации
     * @param info Текст информации
     */
    private void info(String info){
        JOptionPane.showMessageDialog(frame.getContentPane(), info, "Обновление клиента", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Отобразить окно ошибки
     * @param error Текст ошибки
     */
    private void error(String error){
        status = STATUS.ERROR;
        JOptionPane.showMessageDialog(frame.getContentPane(), error, "Ошибка обновления клиента", JOptionPane.WARNING_MESSAGE);
    }


    //cache
    private String currentClientVersion, lastClientVersion, secureKey;

    /**
     * Текущая версия клиента
     * @return Текущая версия клиента
     */
    private String getCurrentClientVersion(){
        if (currentClientVersion == null && status != STATUS.ERROR){
            currentClientVersion = executeQuery("select c.version from client c where c.mac = ?", getCurrentMAC());
        }
        return currentClientVersion;
    }

    /**
     * Последняя версия клиента исходя из последней синхронизации с сервером
     * @return Последняя версия клиента
     */
    private String getLastClientVersion(){
        if (lastClientVersion == null && status != STATUS.ERROR){
            lastClientVersion = executeQuery("select max(u.version) from client_update u where u.version >= ?", getCurrentClientVersion());
        }

        return lastClientVersion;
    }

    /**
     * Регистрационный ключ клиента
     * @return Регистрационный ключ клиента
     */
    private String getSecureKey(){
        if (secureKey == null && status != STATUS.ERROR){
            secureKey = executeQuery("select c.secure_key from client c where c.mac = ?", getCurrentMAC());
        }

        return secureKey;
    }

    /**
     * Новый обновления
     * @return true, если синхронизирована информация о новом обновлении
     */
    private boolean hasUpdate(){
        return getLastClientVersion() != null &&  !getLastClientVersion().equals(getCurrentClientVersion());
    }

    /**
     * Выполнить SELECT запрос
     * @param query SQL запрос
     * @param param Текстовые параметры запроса
     * @return Первая колонка первой строчки
     */
    private String executeQuery(String query, String... param){
        return (String) executeSQL(false, query, param);
    }

    /**
     * Выполнить UPDATE запрос
     * @param query SQL запрос
     * @param param Текстовые параметры запроса
     * @return Первая колонка первой строчки
     */
    private int executeUpdate(String query, String... param){
        return (Integer) executeSQL(true, query, param);
    }

    /**
     * Выполнить SELECT или UPDATE запрос
     * @param update true если UPDATE запрос, false если SELECT запрос
     * @param query SQL запрос
     * @param param Текстовые параметры запроса
     * @return Первая колонка первой строчки
     */
    private Object executeSQL(boolean update, String query, String... param){
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();

            ps = connection.prepareStatement(query);
            if (param != null){
                int index = 0;
                for (String p : param){
                    ps.setString(++index, p);
                }
            }

            if (update){
                return ps.executeUpdate();
            }else{
                ResultSet rs = ps.executeQuery();
                rs.next();

                return rs.getString(1);
            }
        } catch (SQLException e) {
            log.error("Ошибка соединения с базой данных", e);
            error("Ошибка соединения с базой данных");
        } finally {
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    //nothing
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    //nothing
                }
            }
        }

        return null;
    }

    /**
     * Создать JDBC соединение
     * @return JDBC Соединение
     * @throws SQLException SQL исключение
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Внутренний класс для использлвания дополнительной информации о файле обновления
     */
    private class UpdateItem{
        String name;
        String packaging;
        String checkSum;

        private UpdateItem(String name, String packaging, String checkSum) {
            this.name = name;
            this.packaging = packaging;
            this.checkSum = checkSum;
        }
    }

    /**
     * Список файлов требуемых для обновления
     * @param version Версия обновления клиента
     * @return Список файлов требуемых для обновления
     */
    private List<UpdateItem> getDownloadFileNames(String version){
        String query = "select client_update_item.name, client_update_item.packaging, client_update_item.check_sum " +
                "from client_update_item left join client_update " +
                "on (client_update_item.update_id = client_update.id) " +
                "where client_update.version = ?";

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();

            ps = connection.prepareStatement(query);
            if (version != null){
                ps.setString(1, version);
            }

            ResultSet rs = ps.executeQuery();

            java.util.List<UpdateItem> list = new ArrayList<UpdateItem>();

            while(rs.next()){
                list.add(new UpdateItem(rs.getString(1), rs.getString(2), rs.getString(3)));
            }

            return list;
        } catch (SQLException e) {
            log.error("Ошибка соединения с базой данных", e);
            error("Ошибка соединения с базой данных");
        } finally {
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    //nothing
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    //nothing
                }
            }
        }

        return null;
    }

    /**
     * Запуск процесса обновления
     */
    private void update(){
        Runnable doWorkRunnable = new Runnable() {
            public void run() {
                if (status != STATUS.ERROR && !validate(false)){
                    download();
                }
                if (status != STATUS.ERROR){
                    install();
                }
                if (status != STATUS.ERROR){
                    progressBar.setString("Установка завершена успешно");
                    info("Установка завершена успешно");
                    progressBar.setVisible(false);

                    hide();
                }

                updateButton.setVisible(!hasUpdate());
                cancelButton.setVisible(true);
            }
        };

        new Thread(doWorkRunnable).start();
    }

    /**
     * Загрузка файлов новой версии
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void download(){
        final List<UpdateItem> updateItems = getDownloadFileNames(getLastClientVersion());

        if (updateItems == null){
            return;
        }

        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        for (final UpdateItem ui : updateItems){
            Map<String, String> params = new HashMap<String, String>();
            params.put("secureKey", getSecureKey());
            params.put("version", getLastClientVersion());
            params.put("name", ui.name);

            File dir = new File(updateDir, getLastClientVersion());
            dir.mkdirs();

            log.info("updateDir = {}", dir.getAbsolutePath());

            File file = new File(dir, ui.name);

            try {
                final DownloadClient dc = new DownloadClient(new URL(SERVER_UPDATE_URL), params, file);

                dc.addObserver(new Observer(){

                    @Override
                    public void update(Observable o, Object arg) {
                        int value = dc.getDownloadedSize() * 100 / dc.getTotalSize();
                        progressBar.setString("Загрузка файла " + ui.name + "... " + value + "%");
                        progressBar.setValue(value);
                        progressBar.repaint();
                    }
                });

                dc.download();

                if(dc.getStatus() == DownloadClient.STATUS.ERROR){
                    progressBar.setString("Ошибка загрузки");
                    error("Ошибка загрузки. Проверьте соединение с сервером.");
                    return;
                }
            } catch (MalformedURLException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        progressBar.setString("Загрузка завершена");
    }

    /**
     * Проверка файлов на наличие и контрольной суммы
     * @param remove Удалить файл, если неверная контрольная сумма
     * @return true, если файл сохранен и совпадает контрольная сумма
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private boolean validate(boolean remove){
        progressBar.setValue(0);
        progressBar.setString("Проверка обновлений...");

        for (UpdateItem ui : getDownloadFileNames(getLastClientVersion())){
            File file = new File(updateDir, getLastClientVersion() + "/" + ui.name);

            try {
                if (file.exists()){
                    String checkSum = DigestUtils.md5Hex(new FileInputStream(file));

                    log.info("validate file = {}, client checkSum = {}, server checkSum = {}",
                            new String[]{file.getAbsolutePath(), checkSum, ui.checkSum});

                    if (!checkSum.equals(ui.checkSum)){
                        if (remove){
                            file.delete();
                        }
                        return false;
                    }
                }else{
                    return false;
                }
            } catch (IOException e) {
                log.error("Ошибка проверки файла", e);
                return false;
            }
        }

        return true;
    }

    /**
     * Удаление старой версии, установка новой версии
     */
    private void install(){
        if (validate(true)){
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);

            //undeploy client
            progressBar.setString("Остановка приложения...");
            iServer.undeployClient();

            //files copy
            progressBar.setString("Установка обновлений...");
            for (UpdateItem ui :  getDownloadFileNames(getLastClientVersion())){
                if ("WAR".equalsIgnoreCase(ui.packaging)){
                    installWar(ui.name);
                }
            }

            //set version
            currentClientVersion = null;
            int count = executeUpdate("update client set version = ? where mac = ?", getLastClientVersion(), getCurrentMAC());
            if (count != 1){
                error("Ошибка соединения с базой данных");
                //TODO backup
            }

            currentVersionLabel.setText("Текущая версия клиента: " + getCurrentClientVersion());

            //deploy client
            progressBar.setString("Запуск приложения...");
            iServer.deployClient();

            progressBar.setIndeterminate(false);
        }else{
            progressBar.setString("Ошибка проверки контрольной суммы");
            error("Ошибка проверки контрольной суммы файлов обновлений.");
        }
    }

    /**
     * Распакова и копирование архива приложения
     * @param fileName Имя файла архива
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void installWar(String fileName) {
        final int BUFFER = 2024;

        try {
            //remove client deploy dir
            deleteDir(clientDir);
            clientDir.mkdirs();

            log.info("clientDir = {}", clientDir.getAbsolutePath());

            File file = new File(updateDir, getLastClientVersion() + "/" + fileName);

            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                log.debug("Extracting: {} ", entry);

                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                if (entry.isDirectory()){
                    File dir = new File(clientDir, entry.getName());
                    dir.mkdirs();
                }else{
                    FileOutputStream fos = new FileOutputStream(new File(clientDir, entry.getName()));
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (Exception e) {
            log.error("Ошибка распаковки архива", e);
            error("Ошибка распаковки архива");
        }
    }

    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * MAC адрес клиента
     * @return MAC адрес клиента
     */
    private String getCurrentMAC() {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            String mac = "";
            if (networkInterface.getHardwareAddress() != null) {
                for (byte m : networkInterface.getHardwareAddress()) {
                    mac += String.format("%02x-", m);
                }
            }
            if (!mac.isEmpty()) {
                mac = mac.substring(0, mac.length() - 1).toUpperCase();
                return mac;
            }
        } catch (SocketException e) {
            log.error("Ошибка получения mac адреса", e);
            log.error(e.getMessage(), e);
        } catch (UnknownHostException e) {
            log.error("Ошибка получения mac адреса", e);
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
