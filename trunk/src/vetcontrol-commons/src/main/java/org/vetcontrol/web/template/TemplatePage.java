package org.vetcontrol.web.template;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ContainerInfo;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;

import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 18.12.2009 19:14:31
 */
public abstract class TemplatePage extends WebPage implements IMarkupResourceStreamProvider{
    private final DefaultMarkupResourceStreamProvider defaultMarkupResourceStreamProvider;

    public TemplatePage() {
        List<Component> components = ((TemplateWebApplication)getApplication()).getTemplateComponents();

        if (components != null){
            for (Component component : components){
                add(component);
            }
        }
        defaultMarkupResourceStreamProvider = new DefaultMarkupResourceStreamProvider();
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (TemplatePage.class.equals(containerClass)){
            IResourceStream templateMarkup = ((TemplateWebApplication)getApplication()).getTemplateMarkup();
            if (templateMarkup != null){
                return new MarkupResourceStream(templateMarkup, new ContainerInfo(container), containerClass);
            }                                                                                                                            
        }
        return defaultMarkupResourceStreamProvider.getMarkupResourceStream(container, containerClass);
    }
}
