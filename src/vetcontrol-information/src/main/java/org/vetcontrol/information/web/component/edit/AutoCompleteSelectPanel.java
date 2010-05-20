/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.component.edit;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.collections.MicroMap;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.vetcontrol.information.web.util.Constants;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.information.web.model.StringCultureModel;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.book.BeanPropertyUtil;
import org.vetcontrol.book.Property;
import org.vetcontrol.book.ShowBooksMode;

/**
 *
 * @author Artem
 */
public class AutoCompleteSelectPanel<T> extends Panel {

    /**
     * Message format example: ${property} is incorrect.
     */
    private static final String ERROR_PROPERTY_PARAMETER = "property";

    private static final String SUCH_REFERENCE_IS_NOT_EXIST_ERROR_KEY = AutoCompleteSelectPanel.class.getSimpleName() + ".such_reference_is_not_exist";

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    private final IModel<String> exampleModel;

    private final T example;

    private AbstractAutoCompleteTextField<T> autoCompleteTextField;

    public AutoCompleteSelectPanel(String id, final IModel<T> model, final Property property, boolean enabled, final Locale systemLocale) {
        super(id);

        try {

            AutoCompleteSettings settings = new AutoCompleteSettings();
            settings.setAdjustInputWidth(false);

            final AbstractAutoCompleteTextRenderer<T> renderer = new AbstractAutoCompleteTextRenderer<T>() {

                @Override
                protected String getTextValue(T object) {
                    return AutoCompleteSelectPanel.this.getTextValue(object, property, systemLocale);
                }

                @Override
                protected void renderChoice(T object, Response response, String criteria) {
                    String text = "";
                    if (Strings.isEmpty(property.getBookReferencePattern())) {
                        text = getTextValue(object);
                    } else {
                        text = BeanPropertyUtil.applyPattern(property.getBookReferencePattern(), object, systemLocale);
                    }
                    response.write(text.length() <= Constants.AUTO_COMPLETE_SELECT_MAX_LENGTH ? text
                            : text.substring(0, Constants.AUTO_COMPLETE_SELECT_MAX_LENGTH) + Constants.CONTINUE);
                }
            };

            example = (T) property.getType().newInstance();
            final Property prop = BeanPropertyUtil.getPropertyByName(property.getType(), property.getReferencedField());
            if (prop.isLocalizable()) {
                exampleModel = new StringCultureModel(new PropertyModel(example, prop.getName()));
            } else if (prop.isBookReference()) {
                throw new RuntimeException("Property is book reference. This case was not handled.");
                //not real case.
            } else {
                //simple property
                exampleModel = new PropertyModel<String>(example, prop.getName());
            }

            IModel<String> autoCompleteTextFieldModel = new Model<String>() {

                private String object;

                @Override
                public String getObject() {
                    T objectModel = model.getObject();
                    if (objectModel != null) {
                        return getTextValue(objectModel, property, systemLocale);
                    }
                    return object;
                }

                @Override
                public void setObject(String object) {
                    model.setObject(autoCompleteTextField.findChoice());
                    this.object = object;
                }
            };

            autoCompleteTextField = new AbstractAutoCompleteTextField<T>("autoCompleteTextField",
                    autoCompleteTextFieldModel, String.class, renderer, settings) {

                @Override
                protected List<T> getChoiceList(String searchTextInput) {
                    if (!Strings.isEmpty(searchTextInput)) {
                        exampleModel.setObject(searchTextInput);
                        return bookViewDAO.getContent(example, 0, Constants.AUTO_COMPLETE_TEXT_FIELD_MAX_ITEMS, prop.getName(), true, systemLocale,
                                ShowBooksMode.ENABLED);
                    } else {
                        return Collections.emptyList();
                    }
                }

                @Override
                protected String getChoiceValue(T choice) throws Throwable {
                    return getTextValue(choice, property, systemLocale);
                }

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    Object o = model.getObject();
                    if (o != null) {
                        String title = "";
                        if (Strings.isEmpty(property.getBookReferencePattern())) {
                            title = AutoCompleteSelectPanel.this.getTextValue(o, property, systemLocale);
                        } else {
                            title = BeanPropertyUtil.applyPattern(property.getBookReferencePattern(), o, systemLocale);
                        }
                        tag.put("title", title);
                    }
                }
            };
            autoCompleteTextField.setEnabled(enabled);
            autoCompleteTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.addComponent(autoCompleteTextField);
                }
            });
            autoCompleteTextField.setLabel(new DisplayPropertyLocalizableModel(property, this));
            autoCompleteTextField.setRequired(!property.isNullable());
            autoCompleteTextField.setOutputMarkupId(true);

            //add validator
            autoCompleteTextField.add(new AbstractValidator() {

                @Override
                protected void onValidate(IValidatable validatable) {
                    if (autoCompleteTextField.findChoice() == null) {
                        String localizableProperty = new DisplayPropertyLocalizableModel(property, AutoCompleteSelectPanel.this).getObject();
                        Map<String, Object> params = new MicroMap<String, Object>(ERROR_PROPERTY_PARAMETER, localizableProperty);
//                        error(validatable, SUCH_REFERENCE_IS_NOT_EXIST_ERROR_KEY, params);

                        ValidationError error = new ValidationError().addMessageKey(SUCH_REFERENCE_IS_NOT_EXIST_ERROR_KEY);
                        error.setVariables(params);
                        autoCompleteTextField.error((IValidationError) error);
                    }
                }
            });

            add(autoCompleteTextField);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTextValue(Object object, Property property, Locale systemLocale) {
        return BeanPropertyUtil.getPropertyAsString(object, property, systemLocale);
    }
}
