package org.vetcontrol.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.dao.ILocaleDAO;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.01.2010 23:05:55
 */
public class UKTZEDField extends Panel {
    private static final Logger log = LoggerFactory.getLogger(UKTZEDField.class);

    private final static int MAX_ITEM = 5;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "CargoTypeBean")
    private CargoTypeBean cargoTypeBean;

    public UKTZEDField(String id, final IModel<CargoType> model, final Component... ajaxUpdate) {
        super(id, model);

        final Locale system = localeDAO.systemLocale();

        AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
        autoCompleteSettings.setAdjustInputWidth(false);

        final AutoCompleteTextField<String> uktzed = new AutoCompleteTextField<String>("uktzed",
                new Model<String>(model.getObject() != null ? model.getObject().getCode() : ""),
                autoCompleteSettings){

            @Override
            protected Iterator<String> getChoices(String input) {
                List<CargoType> cargoTypes = cargoTypeBean.getCargoTypesByCode(input, MAX_ITEM);
                List<String> choices = new ArrayList<String>();
                for (CargoType ct : cargoTypes){
                    choices.add(ct.getCode() + "\t" + ct.getDisplayName(getLocale(), system));
                }

                return choices.iterator();
            }
        };

        uktzed.setOutputMarkupId(true);
        add(uktzed);

        final AutoCompleteTextField<String> name = new AutoCompleteTextField<String>("name",
                new Model<String>(model.getObject() != null ? model.getObject().getDisplayName(getLocale(), system) : ""),
                autoCompleteSettings){

            @Override
            protected Iterator<String> getChoices(String input) {
                List<CargoType> cargoTypes = cargoTypeBean.getCargoTypesByName(input, MAX_ITEM);
                List<String> choices = new ArrayList<String>();
                for (CargoType ct : cargoTypes){
                    choices.add(ct.getCode() + "\t" + ct.getDisplayName(getLocale(), system));
                }

                return choices.iterator();
            }
        };
        name.setOutputMarkupId(true);
        add(name);

        uktzed.add(new AjaxFormComponentUpdatingBehavior("onchange"){

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String input = uktzed.getModelObject();

                if (input == null) return;

                String code = input.indexOf('\t') > -1 ?  input.substring(0, input.indexOf('\t')) : input;

                CargoType cargoType = cargoTypeBean.getCargoType(code);
                model.setObject(cargoType);

                name.setModelObject(cargoType != null ? cargoType.getDisplayName(getLocale(), system) : "");
                target.addComponent(name);

                if (cargoType != null){
                    uktzed.setModelObject(code);
                    target.addComponent(uktzed);
                }

                for (Component component : ajaxUpdate){
                    target.addComponent(component);
                }

            }
        });

        name.add(new AjaxFormComponentUpdatingBehavior("onchange"){

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String input = name.getModelObject();

                if (input == null) return;

                if (input.indexOf('\t') > -1){
                    String code = input.substring(0, input.indexOf('\t'));

                    CargoType cargoType = cargoTypeBean.getCargoType(code);
                    model.setObject(cargoType);

                    if (cargoType != null){
                        uktzed.setModelObject(code);
                        name.setModelObject(cargoType.getDisplayName(getLocale(), system));
                        target.addComponent(uktzed);
                        target.addComponent(name);
                    }else{
                        cargoType = cargoTypeBean.getCargoType(uktzed.getModelObject());
                        name.setModelObject(cargoType != null ? cargoType.getDisplayName(getLocale(), system) : "");
                        target.addComponent(name);
                    }

                    for (Component component : ajaxUpdate){
                        target.addComponent(component);
                    }
                }
            }
        });

    }

}
