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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.User;
import org.vetcontrol.service.UserProfileBean;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 18.12.2009 19:14:31
 *
 * Суперкласс для отображения страниц независимых модулей в едином шаблоне.
 * Для инициализации шаблона наследники должны вызывать метод super().
 */
public abstract class TemplatePage extends WebPage{
    private static final Logger log = LoggerFactory.getLogger(TemplatePage.class);

    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;

    public TemplatePage() {
        add(new ListView<ITemplateMenu>("sidebar", newTemplateMenus()){
            @Override
            protected void populateItem(ListItem<ITemplateMenu> item) {
                item.add(new TemplateMenu("menu_placeholder", "menu", this, item.getModelObject()));
            }
        });

        String userdetails = "[Гость]";
        try {
            final User user = userProfileBean.getCurrentUser();
            userdetails = user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleName()
                    + " [" + user.getLogin() + "]";
        } catch (Exception e) {
            log.warn("Пользователь не авторизован", e);
        }

        add(new Label("userdetails",userdetails));

        add(new Form("exit"){
            @Override
            public void onSubmit() {
                getSession().invalidateNow();
                setResponsePage(getApplication().getHomePage());
            }
        });
    }

    /**
     * Боковая панель с меню, которое устанавливается в конфигурационном файле.
     */
    public class TemplateMenu extends Fragment{
        public TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);
            add(new Label("menu_title", menu.getTitle(getLocale())));
            add(new ListView<ITemplateLink>("menu_items", menu.getTemplateLinks(getLocale())){

                @Override
                protected void populateItem(ListItem<ITemplateLink> item) {
                    BookmarkablePageLink link = new BookmarkablePageLink<Class<? extends  Page>>("link", item.getModelObject().getPage(),
                            item.getModelObject().getParameters());
                    link.add(new Label("label", item.getModelObject().getLabel(getLocale())));                    
                    item.add(link);
                }
            });
        }
    }

    //Загрузка списка классов меню из файла конфирурации и их создание
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

    /**
     * Проверка роли пользователя для отображения меню модуля.
     * @param menuClass Класс меню.
     * @return Отображать ли меню пользователю в зависимости от его роли.
     */
    private boolean isTemplateMenuAuthorized(Class<?> menuClass){
        boolean authorized = true;

        final AuthorizeInstantiation classAnnotation = menuClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            authorized = ((TemplateWebApplication)getApplication()).hasAnyRole(new Roles(classAnnotation.value()));
        }

        return authorized;
    }



}
