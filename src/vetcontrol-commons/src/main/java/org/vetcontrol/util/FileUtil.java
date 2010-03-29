package org.vetcontrol.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.03.2010 5:44:30
 */
public class FileUtil {
    private static File uploadTmpFolder;
    private static File uploadClientUpdateFolder;

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static File getUploadTmpFolder(){
        if (uploadTmpFolder == null){
            uploadTmpFolder = new File(System.getProperty("java.io.tmpdir"), "vetcontrol-tmp");
            uploadTmpFolder.mkdirs();
        }

        return uploadTmpFolder;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static File getClientUpdateFolder(){
        if (uploadClientUpdateFolder == null){
            try {
                uploadClientUpdateFolder = new File(new URI(System.getProperty("com.sun.aas.instanceRootURI") + "/vetcontrol-upload"));
                uploadClientUpdateFolder.mkdirs();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return uploadClientUpdateFolder;
    }

    public static File getUpdateFile(String version, String name){
        return new File(getClientUpdateFolder(), version + "/" + name);
    }

}
