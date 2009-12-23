/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.util.web.Property;

/**
 *
 * @author Artem
 */
//TODO: add required to text fields.
//TODO: add text area for long text value.
public final class LocalizableTextPanel extends Panel {

    public LocalizableTextPanel(String id, IModel model, final Property prop) {
        super(id);

        Object o = model.getObject();
        List<StringCulture> list = (List<StringCulture>) o;

        add(new ListView("localizableStrings", list) {

            @Override
            protected void populateItem(ListItem listItem) {
                StringCulture culture = (StringCulture) listItem.getModelObject();

                Label label = new Label("lang", new Locale(culture.getId().getLocale()).getDisplayLanguage(getLocale()));
                listItem.add(label);

                TextField textField = new TextField("simpleTextValue", new PropertyModel(culture, "value"));
                if(prop.getLength() > 0){
                    textField.add(new SimpleAttributeModifier("maxlength", String.valueOf(prop.getLength())));
                }
                listItem.add(textField);
            }
        });

    }
}
