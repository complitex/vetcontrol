/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.beans.IntrospectionException;
import java.util.Date;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.information.util.web.BeanPropertyUtil;
import org.vetcontrol.information.util.web.Property;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public abstract class BookEntryFormControl extends FormComponentPanel {

    private static final Logger log = LoggerFactory.getLogger(BookEntryFormControl.class);

    public BookEntryFormControl(String id, final IModel model) throws IntrospectionException {
        super(id, model);

        List<Property> filtered = BeanPropertyUtil.filter(model.getObject().getClass());

        add(new ListView<Property>("bookFields", filtered) {

            @Override
            protected void populateItem(ListItem<Property> item) {
                Property prop = item.getModelObject();

                item.add(new Label("bookFieldDesc", new DisplayPropertyLocalizableModel(model.getObject().getClass(), prop.getName())));

                boolean isSimpleText = false;
                boolean isDate = false;
                boolean isLocalizableText = false;

                if (prop.getType().equals(String.class) || prop.getType().equals(int.class) || prop.getType().equals(Integer.class)
                        || prop.getType().equals(long.class) || prop.getType().equals(Long.class)) {
                    isSimpleText = true;
                } else if (Date.class.isAssignableFrom(prop.getType())) {
                    isDate = true;
                } else if (prop.isLocalizable()) {
                    isLocalizableText = true;
                }

                IModel m = new PropertyModel(model, prop.getName());

                Panel datePanel = null;
                Panel textPanel = null;
                Panel localizableTextPanel = null;
                //choose what panel is editable:
                if (isSimpleText) {
                    textPanel = new TextPanel("textPanel", m, prop);
                    datePanel = new EmptyPanel("datePanel");
                    localizableTextPanel = new EmptyPanel("localizableTextPanel");
                } else if (isLocalizableText) {
                    textPanel = new EmptyPanel("textPanel");
                    datePanel = new EmptyPanel("datePanel");
                    localizableTextPanel = new LocalizableTextPanel("localizableTextPanel", m, prop);
                } else if (isDate) {
                    textPanel = new EmptyPanel("textPanel");
                    datePanel = new DatePanel("datePanel", m, prop);
                    localizableTextPanel = new EmptyPanel("localizableTextPanel");
                }

                item.add(datePanel);
                item.add(textPanel);
                item.add(localizableTextPanel);

            }
        });

        add(
                new SubmitLink("saveOrUpdateBook") {

                    @Override
                    public void onSubmit() {
                        saveOrUpdate();
                    }
                });

        add(
                new SubmitLink("cancel") {

                    @Override
                    public void onSubmit() {
                        cancel();
                    }
                }.setDefaultFormProcessing(
                false));
    }

    public abstract void saveOrUpdate();

    public abstract void cancel();
}
