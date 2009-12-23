package org.vetcontrol.web.template;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 22.12.2009 23:57:25
 */
public interface ITemplateMenu extends Serializable{
    public String getTitle(Locale locale);

    public List<ITemplateLink> getTemplateLinks(Locale locale);
}
