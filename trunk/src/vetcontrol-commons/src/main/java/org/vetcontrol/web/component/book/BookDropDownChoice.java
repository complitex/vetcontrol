package org.vetcontrol.web.component.book;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.vetcontrol.entity.Localizable;

import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.05.2010 15:57:14
 */
@SuppressWarnings({"JavaDoc"})
public class BookDropDownChoice<T extends Localizable> extends DropDownChoice<T> {
    private Locale system;

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String)
     */
    protected BookDropDownChoice(String id) {
        super(id);
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, java.util.List)
     */
    public BookDropDownChoice(String id, List<? extends T> choices, Locale system) {
        super(id, choices);
        setChoiceRenderer(new BookChoiceRenderer<T>(system));
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String,
     *      java.util.List , org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    protected BookDropDownChoice(String id, List<? extends T> data, IChoiceRenderer<? super T> iChoiceRenderer) {
        super(id, data, iChoiceRenderer);
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, org.apache.wicket.model.IModel , java.util.List)
     */
    public BookDropDownChoice(String id, IModel<T> tiModel, List<? extends T> choices, Locale system) {
        super(id, tiModel, choices);
        setChoiceRenderer(new BookChoiceRenderer<T>(system));
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, org.apache.wicket.model.IModel , java.util.List ,
     *      org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    protected BookDropDownChoice(String id, IModel<T> tiModel, List<? extends T> data, IChoiceRenderer<? super T> iChoiceRenderer) {
        super(id, tiModel, data, iChoiceRenderer);
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, org.apache.wicket.model.IModel)
     */
    public BookDropDownChoice(String id, IModel<? extends List<? extends T>> choices, Locale system) {
        super(id, choices);
        setChoiceRenderer(new BookChoiceRenderer<T>(system));
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, org.apache.wicket.model.IModel , org.apache.wicket.model.IModel)
     */
    public BookDropDownChoice(String id, IModel<T> tiModel, IModel<? extends List<? extends T>> choices, Locale system) {
        super(id, tiModel, choices);
        setChoiceRenderer(new BookChoiceRenderer<T>(system));
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String,
     *      org.apache.wicket.model.IModel , org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    protected BookDropDownChoice(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> iChoiceRenderer) {
        super(id, choices, iChoiceRenderer);
    }

    /**
     * @see org.apache.wicket.markup.html.form.AbstractChoice#AbstractChoice(String, org.apache.wicket.model.IModel ,
     *      org.apache.wicket.model.IModel , org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    protected BookDropDownChoice(String id, IModel<T> tiModel, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> iChoiceRenderer) {
        super(id, tiModel, choices, iChoiceRenderer);
    }
}
