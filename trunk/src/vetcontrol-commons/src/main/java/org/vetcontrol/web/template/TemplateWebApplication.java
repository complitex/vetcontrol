package org.vetcontrol.web.template;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.util.resource.IResourceStream;
import org.vetcontrol.web.security.ServeltAuthWebApplication;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 18.12.2009 19:15:05
 */
public abstract class TemplateWebApplication extends ServeltAuthWebApplication {

    protected TemplateWebApplication() {
    }

    public abstract List<Component> getTemplateComponents();

    /**
     * Example: return UrlResourceStream(MyPage.class.getResource("template.html"));
     * @return Template markup resource stream
     */
    public abstract IResourceStream getTemplateMarkup();
    
}
