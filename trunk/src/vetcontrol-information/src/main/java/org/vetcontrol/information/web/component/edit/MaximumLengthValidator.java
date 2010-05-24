/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.util.Map;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 *
 * @author Artem
 */
public class MaximumLengthValidator<T> extends AbstractValidator<T> {

    private final int maximum;

    public MaximumLengthValidator(int maximum) {
        this.maximum = maximum;
    }

    @Override
    protected void onValidate(IValidatable<T> validatable) {
        T value = validatable.getValue();
        String valueAsString = asString(value);
        if (valueAsString.length() > maximum) {
            error(validatable);
        }
    }

    protected String asString(T value){
        return String.valueOf(value);
    }

    @Override
    protected String resourceKey() {
        return "MaximumLengthValidator";
    }

    @Override
    protected Map<String, Object> variablesMap(IValidatable<T> validatable) {
        final Map<String, Object> map = super.variablesMap(validatable);
        map.put("maximum", new Integer(maximum));
        map.put("length", new Integer(asString(validatable.getValue()).length()));
        return map;
    }
}
