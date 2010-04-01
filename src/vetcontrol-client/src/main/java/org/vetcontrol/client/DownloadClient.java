package org.vetcontrol.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Observable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.03.2010 11:09:30
 *
 */
public class DownloadClient extends Observable{
    private static final Logger log = LoggerFactory.getLogger(DownloadClient.class);
    
    private static final int MAX_BUFFER_SIZE = 8*1024;

    public static enum STATUS{DOWNLOADING, COMPLETE, ERROR}

    private URL downloadFrom;
    private Map<String, String> postParams;
    private File saveTo;
    private STATUS status;
    private int totalSize = -1;
    private int downloadedSize = 0;

    public DownloadClient(URL downloadFrom, Map<String, String> postParams, File saveTo) {
        this.downloadFrom = downloadFrom;
        this.postParams = postParams;
        this.saveTo = saveTo;
    }

    public void download(){
        if (saveTo.exists() && saveTo.length() > MAX_BUFFER_SIZE){
            downloadedSize = (int) (saveTo.length() - MAX_BUFFER_SIZE);
        }

        status = STATUS.DOWNLOADING;

        process();
    }

    private void process() {
        RandomAccessFile file = null;
        InputStream inputStream = null;
        OutputStreamWriter outputStreamWriter = null;

        try {
            // Open connection to URL.
            HttpURLConnection connection = (HttpURLConnection) downloadFrom.openConnection();

             // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");

            //Post parameters
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            String post = "";
            for (String key : postParams.keySet()){
                post += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(postParams.get(key), "UTF-8");
            }

            outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(post);
            outputStreamWriter.flush();
            
            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                status = STATUS.ERROR;
                stateChanged();
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                status = STATUS.ERROR;
                stateChanged();
            }

            //Set the size for this download if it hasn't been already set.
            if (totalSize == -1) {
                if (downloadedSize > 0){
                    totalSize = contentLength + downloadedSize;                    
                }else{
                    totalSize = contentLength;
                }

                stateChanged();
            }

            // Open file and seek to the end of it.
            file = new RandomAccessFile(saveTo, "rw");
            file.seek(downloadedSize);

            inputStream = connection.getInputStream();
            while (status == STATUS.DOWNLOADING) {
                Thread.sleep(100);

                //Size buffer according to how much of the file is left to download.
                byte buffer[];
                if (totalSize - downloadedSize > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[totalSize - downloadedSize];
                }

                // Read from server into buffer.
                int read = inputStream.read(buffer);
                if (read == -1)
                    break;

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloadedSize += read;
                stateChanged();
            }

            // Change status to complete if this point was eached because downloading has finished.
            if (status == STATUS.DOWNLOADING) {
                status = STATUS.COMPLETE;
                stateChanged();
            }
        } catch (Exception e) {
            status = STATUS.ERROR;
            stateChanged();

            log.error("Ошибка загрузки файла", e);
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                    log.error("Ошибка закрытия файла", e);
                }
            }

            // Close connection to server.
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("Ошибка закрытия соединения с сервером", e);
                }
            }

            if (outputStreamWriter != null){
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    log.error("Ошибка закрытия соединения с сервером", e);
                }

            }
        }
    }

    private void stateChanged() {
        setChanged();
        notifyObservers();
    }

    public STATUS getStatus() {
        return status;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getDownloadedSize() {
        return downloadedSize;
    }
}
