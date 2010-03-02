/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.web;

import java.io.Serializable;
import org.apache.wicket.Application;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplateWebApplication;

/**
 *
 * @author Artem
 */
public abstract class CanEditUtil {

    public static boolean canEdit(Serializable bookEntry) {
        TemplateWebApplication application = (TemplateWebApplication) Application.get();
        return application.hasAnyRole(SecurityRoles.INFORMATION_EDIT)
                && !(Boolean) BeanPropertyUtil.getPropertyValue(bookEntry, BeanPropertyUtil.getDisabledPropertyName());
    }
}
