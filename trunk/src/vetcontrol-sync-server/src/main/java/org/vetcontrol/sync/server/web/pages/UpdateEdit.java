package org.vetcontrol.sync.server.web.pages;

import org.apache.commons.codec.binary.Hex;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Update;
import org.vetcontrol.entity.UpdateItem;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.sync.server.service.UpdateBean;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.util.FileUtil;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.vetcontrol.entity.Log.EVENT.EDIT;
import static org.vetcontrol.entity.Log.MODULE.USER;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.03.2010 16:02:56
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UpdateEdit extends FormTemplatePage{
    private static final Logger log = LoggerFactory.getLogger(UpdateEdit.class);

    @EJB(name = "UpdateBean")
    private UpdateBean updateBean;

    @EJB(name = "LogBean")
    private LogBean logBean;

    public UpdateEdit() {
        init(null);
    }

    public UpdateEdit(PageParameters parameters){
        init(parameters != null ? parameters.getAsLong("update_id") : null);
    }

    public void init(final Long id){
        add(new Label("title", new ResourceModel("sync.server.update.edit.title")));
        add(new Label("header", new ResourceModel("sync.server.update.edit.title")));

        add(new FeedbackPanel("messages"));

        //Директории загрузок
        final Folder tmpFolder = new Folder(FileUtil.getUploadTmpFolder());
        final Folder uploadFolder = new Folder(FileUtil.getClientUpdateFolder());

        //Модель данных
        Update updateObject = null;
        try {
            updateObject = (id != null) ? updateBean.getUpdate(id) : new Update();
            if (updateObject.getItems() == null){
                updateObject.setItems(new ArrayList<UpdateItem>());
            }
        } catch (Exception e) {
            getSession().error("Обновление клиента по id = " + id + " не найдено");

            log.error("Обновление клиента по id = " + id + " не найдено", e);
            logBean.error(USER, EDIT, UpdateEdit.class, Update.class,
                    "Обновление клиента не найдено. ID: " + id);
        }

        final String updateVersion = updateObject.getVersion();

        final IModel<Update> model = new Model<Update>(updateObject);

        final List<UpdateItem> removeItems = new ArrayList<UpdateItem>();

        //Форма
        Form form = new Form<Update>("update_edit_form", model);
        form.setMultiPart(true);
        form.setMaxSize(Bytes.megabytes(300));

        add(form);

        //Сохранение
        Button save = new Button("save"){
            @SuppressWarnings({"ResultOfMethodCallIgnored"})
            @Override
            public void onSubmit() {
                try {
                    Update update = model.getObject();

                    //check version unique
                    if (id == null && !updateBean.isUnique(update.getVersion())){
                        
                        getSession().error(getString("sync.server.update.edit.error.not_unique"));

                        return;                        
                    }

                    if (!update.getItems().isEmpty()){
                        File source_dir = new File(tmpFolder, getSession().getId());

                        File destination_dir = new File(uploadFolder, update.getVersion());
                        destination_dir.mkdirs();

                        //move if version changed
                        if (updateVersion != null && !updateVersion.equals(update.getVersion())){
                            File oldDir = new File(uploadFolder, updateVersion);

                            for (File f : oldDir.listFiles()){
                                if (f.isFile()){
                                    File file = new File(destination_dir, f.getName());
                                    file.createNewFile();

                                    copyFile(f, file);

                                    f.delete();
                                }
                            }

                            oldDir.delete();
                        }

                        //if created == null, file is new
                        for (UpdateItem it : update.getItems()){                            
                            if (it.getCreated() == null){
                                it.setUpdate(update);
                                it.setCreated(DateUtil.getCurrentDate());

                                File file = new File(destination_dir, it.getName());
                                file.createNewFile();

                                copyFile(new File(source_dir, it.getName()), file);
                            }
                        }

                        //remove
                        for (UpdateItem it : removeItems){
                            if (it.getCreated() == null){
                                new File(source_dir, it.getName()).delete();
                            }else{
                                new File(destination_dir, it.getName()).delete();
                            }
                        }
                    }

                    update.setCreated(DateUtil.getCurrentDate());

                    updateBean.save(update);

                    setResponsePage(UpdateList.class);

                    getSession().info(getString("sync.server.update.edit.info.save"));
                } catch (Exception e) {
                    getSession().error(getString("sync.server.update.edit.error.save"));
                    log.error("Ошибка сохранения обновления клиента, id: " + id, e);
                    logBean.error(USER, EDIT, UpdateEdit.class, Update.class,
                            "Ошибка сохранения обновления клиента. ID: " + id);
                }
            }
        };
        form.add(save);

        // Добавление файлов
        final FileUploadField uploadField = new FileUploadField("upload_war_field");
        form.add(uploadField);
//
//        //Индикатор загрузки
//        final UploadProgressBar uploadProgressBar = new UploadProgressBar("progress", form);
//        form.add(uploadProgressBar);

        //Загрузка
        Button upload = new Button("upload"){
            @SuppressWarnings({"ResultOfMethodCallIgnored"})
            @Override
            public void onSubmit() {
                FileUpload upload = uploadField.getFileUpload();

                if (upload == null){
                    return;                    
                }

                UpdateItem item = new UpdateItem();
                item.setName(upload.getClientFileName());

                String filename = upload.getClientFileName();
                String ext = filename.substring(filename.lastIndexOf('.')+1, filename.length());

                //Проверка имени
                for (UpdateItem it : model.getObject().getItems()){
                    if (it.getName().equals(filename)){
                        getSession().error(getString("sync.server.update.edit.error.file_added"));
                        return;
                    }
                }

                //Проверка расширения
                if ("WAR".equalsIgnoreCase(ext)){
                    for (UpdateItem it : model.getObject().getItems()){
                        if (UpdateItem.PACKAGING.WAR.equals(it.getPackaging())){
                            getSession().error(getString("sync.server.update.edit.error.war_added"));
                            return;
                        }
                    }

                    item.setPackaging(UpdateItem.PACKAGING.WAR);
                } else if ("JAR".equalsIgnoreCase(ext)){
                    item.setPackaging(UpdateItem.PACKAGING.JAR);
                } else if ("SQL".equalsIgnoreCase(ext)){
                    item.setPackaging(UpdateItem.PACKAGING.SQL);
                } else if ("ZIP".equalsIgnoreCase(ext)){
                    item.setPackaging(UpdateItem.PACKAGING.SQL_ZIP);
                } else{
                    getSession().error(getString("sync.server.update.edit.error.not_supported_ext"));
                    return;
                }

                //Создание временной директории
                File dir = new File(tmpFolder, getSession().getId());
                dir.mkdirs();

                File file = new File(dir, filename);

                //Сохранение файла
                try {
                    file.createNewFile();
                    upload.writeTo(file);

                    //Контрольная сумма                     
                    item.setCheckSum(new String(Hex.encodeHex(md5(new FileInputStream(file)))));

                    model.getObject().getItems().add(item);

                    getSession().info("Файл " + upload.getClientFileName() + " загружен");
                } catch (IOException e) {
                    log.error("Ошибка создания временного файла", e);
                }
            }
        };
        upload.setDefaultFormProcessing(false);
        form.add(upload);

        //Отмена
        Button cancel = new Button("back") {

            @Override
            public void onSubmit() {                
                setResponsePage(UpdateList.class);
            }
        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        //Версия
        RequiredTextField version = new RequiredTextField<String>("sync.server.update.edit.version",  new PropertyModel<String>(model, "version"));
        form.add(version);

        //Тип
        DropDownChoice type = new DropDownChoice<Update.TYPE>("sync.server.update.edit.type", new PropertyModel<Update.TYPE>(model, "type"),
                Arrays.asList(Update.TYPE.values()), new IChoiceRenderer<Update.TYPE>(){

                    @Override
                    public Object getDisplayValue(Update.TYPE type) {
                        return getStringOrKey(type.name());
                    }

                    @Override
                    public String getIdValue(Update.TYPE type, int i) {
                        return type.name();
                    }
                });
        type.setRequired(true);
        form.add(type);

        //Статус
        DropDownChoice active = new DropDownChoice<Boolean>("sync.server.update.edit.active", new PropertyModel<Boolean>(model, "active"),
                Arrays.asList(Boolean.TRUE, Boolean.FALSE), new IChoiceRenderer<Boolean>(){

                    @Override
                    public Object getDisplayValue(Boolean aBoolean) {
                        return getStringOrKey(aBoolean.toString());
                    }

                    @Override
                    public String getIdValue(Boolean aBoolean, int i) {
                        return aBoolean.toString();
                    }
                });
        active.setRequired(true);
        form.add(active);

        //Список загруженных файлов
        final WebMarkupContainer updateItemsContainer = new WebMarkupContainer("update_items_container");
        updateItemsContainer.setOutputMarkupId(true);
        form.add(updateItemsContainer);

        ListView listView = new ListView<UpdateItem>("update_items", new PropertyModel<List<UpdateItem>>(model, "items")){

            @Override
            protected void populateItem(final ListItem<UpdateItem> item) {
                item.add(new Label("name", item.getModelObject().getName()));
                item.add(new AjaxLink("delete"){

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        for (UpdateItem it : model.getObject().getItems()){
                            if (it.getName().equals(item.getModelObject().getName())){
                                removeItems.add(it);
                                model.getObject().getItems().remove(it);
                                break;
                            }
                        }

                        target.addComponent(updateItemsContainer);
                    }
                });
            }
        };
        updateItemsContainer.add(listView);

        form.add(new Spacer("spacer"));
    }

    private void copyFile(File in, File out) throws Exception {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);

        byte[] buf = new byte[1024];
        int i;
        while((i=fis.read(buf))!=-1) {
            fos.write(buf, 0, i);
        }

        fis.close();
        fos.close();
    }

    private byte[] md5(InputStream data){
        int STREAM_BUFFER_LENGTH = 1024;

        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
            int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

            while (read > -1) {
                digest.update(buffer, 0, read);
                read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
            }

            return digest.digest();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
