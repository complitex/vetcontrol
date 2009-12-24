package org.vetcontrol.web.template;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 18.12.2009 19:14:31
 */
public abstract class TemplatePage extends WebPage{
    public TemplatePage() {            
        add(new ListView<ITemplateMenu>("sidebar", newTemplateMenus()){
            @Override
            protected void populateItem(ListItem<ITemplateMenu> item) {
                item.add(new TemplateMenu("menu_placeholder", "menu", this, item.getModelObject()));
            }
        });

        add(new Form("exit"){
            @Override
            public void onSubmit() {
                getSession().invalidateNow();
                setResponsePage(getApplication().getHomePage());
            }
        });
    }

    public class TemplateMenu extends Fragment{
        public TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);
            add(new Label("menu_title", menu.getTitle(getLocale())));
            add(new ListView<ITemplateLink>("menu_items", menu.getTemplateLinks(getLocale())){

                @Override
                protected void populateItem(ListItem<ITemplateLink> item) {
                    BookmarkablePageLink link = new BookmarkablePageLink<Class<? extends  Page>>("link", item.getModelObject().getPage());
                    link.add(new Label("label", item.getModelObject().getLabel(getLocale())));                    
                    item.add(link);
                }
            });
        }
    }

    private List<ITemplateMenu> newTemplateMenus(){
        List<String> classes = ((TemplateWebApplication)getApplication()).getTemplateLoader().getMenuClassNames();
        List<ITemplateMenu> templateMenus = new ArrayList<ITemplateMenu>();

        for (String _class : classes){
            try {
                Class menuClass =  getSession().getClassResolver().resolveClass(_class);

                if (isTemplateMenuAuthorized(menuClass)){
                    ITemplateMenu templateMenu = (ITemplateMenu) menuClass.newInstance();
                    templateMenus.add(templateMenu);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return templateMenus;
    }

    private boolean isTemplateMenuAuthorized(Class<?> menuClass){
        boolean authorized = true;

        final AuthorizeInstantiation classAnnotation = menuClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            authorized = ((TemplateWebApplication)getApplication()).hasAnyRole(new Roles(classAnnotation.value()));
        }

        return authorized;
    }



}
