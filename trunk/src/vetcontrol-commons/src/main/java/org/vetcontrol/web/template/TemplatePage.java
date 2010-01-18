package org.vetcontrol.web.template;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.User;
import org.vetcontrol.service.UserProfileBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.web.component.LocalePicker;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.string.Strings;
import org.odlabs.wiquery.core.commons.CoreJavaScriptResourceReference;
import org.vetcontrol.service.UIPreferences;
import org.vetcontrol.web.component.toolbar.HelpButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 18.12.2009 19:14:31
 *
 * Суперкласс для отображения страниц независимых модулей в едином шаблоне.
 * Для инициализации шаблона наследники должны вызывать метод super().
 */
public abstract class TemplatePage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(TemplatePage.class);
    @EJB(name = "UserProfileBean")
    private UserProfileBean userProfileBean;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    public TemplatePage() {
        add(CSSPackageResource.getHeaderContribution("/css/style.css"));
        add(JavascriptPackageResource.getHeaderContribution(CoreJavaScriptResourceReference.get()));
        add(JavascriptPackageResource.getHeaderContribution(TemplatePage.class, "TemplatePage.js"));

        //locale picker
        add(new LocalePicker("localePicker", localeDAO.all(), localeDAO.systemLocale(), getPreferences()));

        //toolbar
        WebMarkupContainer toolbar = new WebMarkupContainer("toolbar");
        add(toolbar);
        WebMarkupContainer commonPart = new WebMarkupContainer("commonPart");
        toolbar.add(commonPart);

        //add common buttons.
        HelpButton help = new HelpButton("help");
        commonPart.add(help);

        //add page custom buttons.
        List<ToolbarButton> pageToolbarButtonsList = getToolbarButtons("pageToolbarButton");
        if (pageToolbarButtonsList == null) {
            pageToolbarButtonsList = Collections.emptyList();
        }
        Component pagePart = new ListView<ToolbarButton>("pagePart", pageToolbarButtonsList) {

            @Override
            protected void populateItem(ListItem<ToolbarButton> item) {
                item.add(item.getModelObject());
            }
        };
        toolbar.add(pagePart);


        //menu
        add(new ListView<ITemplateMenu>("sidebar", newTemplateMenus()) {

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

        add(new Label("userdetails", userdetails));

        add(new Form("exit") {

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
    public class TemplateMenu extends Fragment {

        private String tagId;

        public TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);
            this.tagId = menu.getTagId();

            add(new Label("menu_title", menu.getTitle(getLocale())));
            add(new ListView<ITemplateLink>("menu_items", menu.getTemplateLinks(getLocale())) {

                @Override
                protected void populateItem(ListItem<ITemplateLink> item) {
                    BookmarkablePageLink link = new BookmarkablePageLink<Class<? extends Page>>("link", item.getModelObject().getPage(),
                            item.getModelObject().getParameters());
                    link.add(new Label("label", item.getModelObject().getLabel(getLocale())));
                    item.add(link);
                }
            });
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (!Strings.isEmpty(tagId)) {
                tag.put("id", tagId);
            }
        }
    }

    //Загрузка списка классов меню из файла конфирурации и их создание
    private List<ITemplateMenu> newTemplateMenus() {
        List<String> classes = ((TemplateWebApplication) getApplication()).getTemplateLoader().getMenuClassNames();
        List<ITemplateMenu> templateMenus = new ArrayList<ITemplateMenu>();

        for (String _class : classes) {
            try {
                Class menuClass = getSession().getClassResolver().resolveClass(_class);

                if (isTemplateMenuAuthorized(menuClass)) {
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
    private boolean isTemplateMenuAuthorized(Class<?> menuClass) {
        boolean authorized = true;

        final AuthorizeInstantiation classAnnotation = menuClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            authorized = ((TemplateWebApplication) getApplication()).hasAnyRole(new Roles(classAnnotation.value()));
        }

        return authorized;
    }

    protected VetControlSession getVetControlSession() {
        return (VetControlSession) getSession();
    }

    protected UIPreferences getPreferences() {
        return getVetControlSession().getPreferences();
    }

    /**
     * Subclass can override method in order to specify custom page toolbar buttons.
     * @return
     */
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return null;
    }
}
