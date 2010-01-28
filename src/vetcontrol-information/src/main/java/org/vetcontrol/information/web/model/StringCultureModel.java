/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.model.IModel;
import org.vetcontrol.entity.StringCulture;
import org.vetcontrol.entity.StringCultureId;

/**
 *
 * @author Artem
 */
public class StringCultureModel implements IModel<String> {

    private IModel model;

    public StringCultureModel(IModel model) {
        this.model = model;
    }

    @Override
    public String getObject() {
        List<StringCulture> strings = (List<StringCulture>) model.getObject();
        if (strings.isEmpty()) {
            return "";
        }
        return strings.get(0).getValue();
    }

    @Override
    public void setObject(String object) {
        List<StringCulture> strings = Arrays.asList(new StringCulture(new StringCultureId(), object));
        model.setObject(strings);
    }

    @Override
    public void detach() {
    }
}
