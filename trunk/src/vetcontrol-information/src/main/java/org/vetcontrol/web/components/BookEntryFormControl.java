/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.components;

import java.beans.IntrospectionException;
import java.util.Date;
import java.util.List;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.util.web.BeanPropertiesFilter;
import org.vetcontrol.util.web.Property;
import org.vetcontrol.web.model.DisplayPropertyLocalizableModel;

/**
 *
 * @author Artem
 */
public abstract class BookEntryFormControl extends FormComponentPanel {

    private static final Logger log = LoggerFactory.getLogger(BookEntryFormControl.class);
    private static final int TEXT_LENGTH_LIMIT = 30;

    public BookEntryFormControl(String id, final IModel model) throws IntrospectionException {
        super(id, model);

        try {
            List<Property> filtered = BeanPropertiesFilter.filter(model.getObject().getClass());

            add(new ListView<Property>("bookFields", filtered) {

                @Override
                protected void populateItem(ListItem<Property> item) {
                    Property prop = item.getModelObject();

                    item.add(new Label("bookFieldDesc", new DisplayPropertyLocalizableModel(model.getObject().getClass(), prop.getName())));

                    boolean writable = true;
                    boolean readable = false;
                    boolean isTextField = false;
                    boolean isTextArea = false;
                    boolean isDateField = false;



                    if (prop.isReadable()) {
                        readable = true;
                        if (!prop.isWritable()) {
                            writable = false;
                        } else {
                            if (prop.getType().equals(String.class)) {
                                if (prop.getLength() > TEXT_LENGTH_LIMIT) {
                                    //it is text area
                                    isTextArea = true;
                                } else {
                                    //it is text field
                                    isTextField = true;
                                }
                            } else if (prop.getType().equals(int.class) || prop.getType().equals(Integer.class) || prop.getType().equals(long.class) || prop.getType().equals(Long.class)) {
                                //it is text field
                                isTextField = true;
                            } else if (Date.class.isAssignableFrom(prop.getType())) {
                                //it is date field
                                isDateField = true;
                            }
                        }
                    }

                    TextField textField = new TextField("bookFieldValue");
                    TextArea textArea = new TextArea("bookFieldBigValue");
                    DatePicker<Date> dateField = new DatePicker<Date>("bookFieldValue");

                    if (readable) {
                        textField.setModel(new PropertyModel(model, prop.getName()));
                        textArea.setModel(new PropertyModel(model, prop.getName()));
                        dateField.setModel(new PropertyModel(model, prop.getName()));
                    }

                    textField.setEnabled(writable);
                    textArea.setEnabled(writable);
                    dateField.setEnabled(writable);

                    textField.setRequired(!prop.isNullable());
                    textArea.setRequired(!prop.isNullable());
                    dateField.setRequired(!prop.isNullable());

                    //choose what to add text field or date field?
                    if (isDateField) {
                        dateField.setButtonImage("images/calendar.gif");
                        dateField.setButtonImageOnly(true);
                        dateField.setShowOn(DatePicker.ShowOnEnum.BOTH);

                        item.add(dateField);
                    } else {
                        if (prop.getLength() != 0) {
                            textField.add(new SimpleAttributeModifier("maxlength", String.valueOf(prop.getLength())));
                        }

                        item.add(textField);
                    }
                    item.add(textArea);

                    //choose what field is not visible:
                    if (isTextField || isDateField) {
                        textArea.setVisible(false);
                    } else {
                        textField.setVisible(false);
                        dateField.setVisible(false);
                    }

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
        } catch (IntrospectionException e) {
            log.error("", e);
            throw e;
        }
    }

    public abstract void saveOrUpdate();

    public abstract void cancel();
}
