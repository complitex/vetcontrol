package org.vetcontrol.web.template;

import org.apache.wicket.Page;

import java.io.Serializable;
import java.util.Locale;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:09:34
 */
public interface ITemplateLink extends Serializable{
    public String getLabel(Locale locale);

    public Class<? extends Page> getPage();
}
