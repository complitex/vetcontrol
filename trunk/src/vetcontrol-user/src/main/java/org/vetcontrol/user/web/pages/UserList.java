package org.vetcontrol.user.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.user.service.UserBean;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.component.paging.PagingNavigator;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 21:23:07
 *
 * Класс используется для отображения списка пользователей с фильтрацией по ключевому слову и сортировкой.
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UserList extends TemplatePage{
    private static final Logger log = LoggerFactory.getLogger(UserList.class);
    @EJB(name = "UserBean")
    private UserBean userBean;
    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    private final static int ITEMS_ON_PAGE = 13;

    public UserList() {
        super();

        add(new Label("title", getString("user.list.title")));

        //Форма фильтра по ключевому слову
        String filter = null;
        final Form<String> filterForm = new Form<String>("filter_form", new Model<String>(filter));
        add(filterForm);
        filterForm.add(new TextField<String>("filter_value", filterForm.getModel()));

        //Модель данных для сортировки и постраничного отображения списка пользователей
        final SortableDataProvider<User> userSort = new SortableDataProvider<User>(){
            @Override
            public Iterator<? extends User> iterator(int first, int count) {
                SortParam sortParam = getSort();
                UserBean.OrderBy order = UserBean.OrderBy.valueOf(sortParam.getProperty());
                String filter = filterForm.getModelObject();

                try {
                    return userBean.getUsers(first, count, order, sortParam.isAscending(), filter).iterator();
                } catch (Exception e) {
                    log.error("Ошибка получения списка пользователей из базы данных",e);
                }
                return null;
            }

            @Override
            public int size() {
                try {
                    return userBean.getUserCount(filterForm.getModelObject()).intValue();
                } catch (Exception e) {
                    log.error("Ошибка получения количества списка пользователей из базы данных", e);
                }
                return 0;
            }

            @Override
            public IModel<User> model(User object) {
                return new Model<User>(object);
            }
        };

        userSort.setSort("LAST_NAME", true);

        //Таблица пользователей
        final DataView<User> userDataView = new DataView<User>("users", userSort, ITEMS_ON_PAGE) {

            @Override
            protected void populateItem(Item<User> userItem) {
                User user = userItem.getModelObject();
                userItem.add(new Label("last_name", user.getLastName()));
                userItem.add(new Label("first_name", user.getFirstName()));
                userItem.add(new Label("middle_name", user.getMiddleName()));
                if (user.getDepartment() != null) {
                    if (user.getDepartment().getNames().isEmpty()) {
                        log.error("user : "+user.getId());
                    }
                    userItem.add(new Label("department",
                            BeanPropertyUtil.getLocalizablePropertyAsString(user.getDepartment().getNames(), localeDAO.systemLocale(), null)));
                } else {
                    userItem.add(new Label("department", "DEVELOPMENT"));
                }
                Link<UserEdit> edit = new BookmarkablePageLink<UserEdit>("edit", UserEdit.class,
                        new PageParameters("user_id=" + user.getId()));
                edit.add(new Label("login", user.getLogin()));
                userItem.add(edit);
                userItem.add(new Label("groups", getGroups(user)));
            }
        };

        add(userDataView);

        //Ссылки для активации сортировки по полям
        add(new OrderByBorder("order_last_name", "LAST_NAME", userSort){
            protected void onSortChanged(){
                userDataView.setCurrentPage(0);
            }
        });
        add(new OrderByBorder("order_first_name", "FIRST_NAME", userSort){
            protected void onSortChanged(){
                userDataView.setCurrentPage(0);
            }
        });
        add(new OrderByBorder("order_middle_name", "MIDDLE_NAME", userSort) {

            protected void onSortChanged() {
                userDataView.setCurrentPage(0);
            }
        });
        add(new OrderByBorder("order_department", "DEPARTMENT", userSort) {

            protected void onSortChanged() {
                userDataView.setCurrentPage(0);
            }
        });
        add(new OrderByBorder("order_login", "LOGIN", userSort) {

            protected void onSortChanged() {
                userDataView.setCurrentPage(0);
            }
        });

        //Панель ссылок для постраничной навигации
        PagingNavigator pagingNavigator = new PagingNavigator("navigator", userDataView);

        add(pagingNavigator);
    }

    /**
     * Генерирует строку списка групп пользователей для отображения
     * @param user Пользователь
     * @return Список групп
     */
    private String getGroups(User user) {
        if (user.getUserGroups() == null || user.getUserGroups().isEmpty()) {
            return getString("user.blocked");
        }

        StringBuilder sb = new StringBuilder();

        for (Iterator<UserGroup> it = user.getUserGroups().iterator();;) {
            sb.append(getString(it.next().getUserGroup()));
            if (!it.hasNext()) {
                return sb.toString();
            }
            sb.append(", ");
        }
    }
}
