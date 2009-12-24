/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.model;

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.model.IModel;
import org.vetcontrol.information.model.StringCulture;
import org.vetcontrol.information.model.StringCultureId;

/**
 *
 * @author Artem
 */
public class StringCultureModel implements IModel {

    private IModel model;

    public StringCultureModel(IModel model) {
        this.model = model;
    }

//    public TestModel(IModel model, Property property) {
//        super(model, property.getName());
//        this.property = property;
//    }
    @Override
    public Object getObject() {
        List<StringCulture> strings = (List<StringCulture>) model.getObject();
        if (strings.isEmpty()) {
            return "";
        }
        return strings.get(0).getValue();
    }

    @Override
    public void setObject(Object object) {
        String value = (String) object;
        List<StringCulture> strings = Arrays.asList(new StringCulture(new StringCultureId(), value));
        model.setObject(strings);
    }

    @Override
    public void detach() {
    }
//    @Override
//    public void setObject(Object object) {
//        if (property.isLocalizable()) {
//            final String expression = propertyExpression();
//            PropertyResolverConverter prc = null;
//            prc = new PropertyResolverConverter(Application.get().getConverterLocator(),
//                    Session.get().getLocale());
//            final String asString = (String) object;
//            List<StringCulture> value = Arrays.asList(new StringCulture(new StringCultureId(), asString));
//            PropertyResolver.setValue(expression, getTarget(), object, prc);
//        } else {
//            super.setObject(object);
//        }
//    }
}
