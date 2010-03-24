package org.vetcontrol.web;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.util.file.Folder;
import org.vetcontrol.web.pages.expired.SessionExpiredPage;
import org.vetcontrol.web.pages.welcome.WelcomePage;
import org.vetcontrol.web.template.TemplateWebApplication;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class VetcontrolWebApplication extends TemplateWebApplication {
    private Folder uploadTmpFolder;
    private Folder uploadClientUpdateFolder;

    @Override
    protected void init() {
        getApplicationSettings().setPageExpiredErrorPage(SessionExpiredPage.class);
        super.init();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public Folder getUploadTmpFolder(){
        if (uploadTmpFolder == null){
            uploadTmpFolder = new Folder(System.getProperty("java.io.tmpdir"), "vetcontrol-tmp");
            uploadTmpFolder.mkdirs();
        }

        return uploadTmpFolder;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public Folder getClientUpdateFolder(){
        if (uploadClientUpdateFolder == null){
            try {
                uploadClientUpdateFolder = new Folder(new URI(System.getProperty("com.sun.aas.instanceRootURI") + "/vetcontrol-upload"));
                uploadClientUpdateFolder.mkdirs();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }                                                 
        }

        return uploadClientUpdateFolder;
    }

    @Override
    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
        return new UploadWebRequest(servletRequest);
    }
}
