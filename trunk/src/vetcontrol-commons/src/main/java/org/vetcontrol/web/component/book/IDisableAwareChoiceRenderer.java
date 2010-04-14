/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.web.component.book;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author Artem
 */
public interface IDisableAwareChoiceRenderer<T> extends IChoiceRenderer<T> {

    boolean isDisabled(T object);
}
