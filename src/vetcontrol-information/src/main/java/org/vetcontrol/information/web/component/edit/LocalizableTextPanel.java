/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.util.Locale;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.util.book.Property;

/**
 *
 * @author Artem
 */
public final class LocalizableTextPanel extends Panel {

    public LocalizableTextPanel(String id, IModel model, final Property prop, final Locale systemLocale) {
        super(id);

        add(new ListView("localizableStrings", model) {

            @Override
            protected void populateItem(ListItem item) {
                StringCulture culture = (StringCulture) item.getModelObject();
                Property currentProp = prop.clone();

                Label label = new Label("lang", new Locale(culture.getId().getLocale()).getDisplayLanguage(getLocale()));
                item.add(label);

                if (!(new Locale(culture.getId().getLocale()).getLanguage().equalsIgnoreCase(systemLocale.getLanguage()))) {
                    currentProp.setNullable(true);
                }

                TextPanel textPanel = new TextPanel("textPanel", new PropertyModel(culture, "value"), currentProp);
                item.add(textPanel);

                WebMarkupContainer requiredContainer = new WebMarkupContainer("bookFieldRequired");
                requiredContainer.setVisible(!currentProp.isNullable());
                item.add(requiredContainer);
            }
        });
    }
}
