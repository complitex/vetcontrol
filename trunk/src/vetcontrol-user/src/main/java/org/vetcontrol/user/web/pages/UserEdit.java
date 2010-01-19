package org.vetcontrol.user.web.pages;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.user.service.UserBean;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.security.SecurityGroup;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.security.SecurityWebListener;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 22:26:17
 *
 * Класс предназначен для отображения формы создания и редактирования пользователя с сохранением
 * изменений в базу данных.
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UserEdit extends FormTemplatePage {
    private static final Logger log = LoggerFactory.getLogger(UserEdit.class);

    @EJB(name = "UserBean")
    private UserBean userBean;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    public UserEdit() {
        super();
        init(null);
    }

    public UserEdit(final PageParameters parameters){
        super();
        init(parameters.getAsLong("user_id"));
    }

    private void init(final Long id){
        add(new Label("title", getString("user.edit.title")));

        add(new FeedbackPanel("messages"));

        //Модель данных
        final LoadableDetachableModel<User> userModel = new LoadableDetachableModel<User>(){
            @Override
            protected User load() {
                try {
                    return (id != null) ? userBean.getUser(id) : new User();
                } catch (Exception e) {
                    log.error("Пользователь по id = " + id + " не найден", e);
                }
                return null;
            }
        };

        //Форма
        Form form = new Form<User>("user_edit_form", userModel);
        
        Button save = new Button("save"){

            @Override
            public void onSubmit() {
                User user = (User)getForm().getModelObject();

                /*Пароль кодируется в MD5. Для нового пользователя пароль равен логину.
                Пароль меняется в базе данныех, если поле пароля не пустое*/

                if (id == null){
                    user.setPassword(DigestUtils.md5Hex(user.getLogin()));
                    if (userBean.containsLogin(user.getLogin())){
                        log.warn("Пользователь с логином: " + user.getLogin() + " уже существует");
                        error(getString("user.edit.contain_login"));
                        return;
                    }
                }else if(user.getChangePassword() != null && !user.getChangePassword().isEmpty()){
                    user.setPassword(DigestUtils.md5Hex(user.getChangePassword()));
                }

                try {
                    boolean toLogout = userBean.isUserAuthChanged(user);

                    userBean.save(user);
                    log.info("Пользователь сохранен: " + user);
                    info(getString("user.info.saved"));

                    if (toLogout){
                        if (logout(user.getLogin())){
                            log.info("Текущая сессия пользователя деактивирована: " + user);
                            info(getString("user.info.logoff"));
                        }
                    }
                } catch (Exception e) {
                    log.error("Ошибка сохранения пользователя в базу данных");
                    error(getString("user.info.error.saved"));
                }
            }

        };
        form.add(save);

        Button cancel = new Button("back"){

            @Override
            public void onSubmit() {
                setResponsePage(UserList.class);
            }

        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);
        add(form);

        RequiredTextField login = new RequiredTextField<String>("user.login", new PropertyModel<String>(userModel, "login"));
        login.setEnabled(id == null);
        login.setRequired(id == null);
        form.add(login);

        PasswordTextField password = new PasswordTextField("user.password", new PropertyModel<String>(userModel, "changePassword"));
        password.setEnabled(id != null);
        password.setRequired(false);
        form.add(password);

        form.add(new RequiredTextField<String>("user.last_name", new PropertyModel<String>(userModel, "lastName")));
        form.add(new RequiredTextField<String>("user.first_name", new PropertyModel<String>(userModel, "firstName")));
        form.add(new RequiredTextField<String>("user.middle_name", new PropertyModel<String>(userModel, "middleName")));

        //Department drop down menu
        List<Department> departments = null;
        try {
            departments = userBean.getDepartments();
        } catch (Exception e) {
            log.error("Ошибка загрузки списка структурных единиц",e);
        }
        DropDownChoice dropDownChoice = new DropDownChoice<Department>("user.department", new PropertyModel<Department>(userModel, "department"),
                departments, new IChoiceRenderer<Department>() {
                    @Override
                    public Object getDisplayValue(Department department) {
                        return BeanPropertyUtil.getLocalizablePropertyAsString(department.getNames(), localeDAO.systemLocale(), null);
                    }

                    @Override
                    public String getIdValue(Department department, int index) {
                        return String.valueOf(department.getId());
                    }
                });

        dropDownChoice.setRequired(true);

        form.add(dropDownChoice);

        //User groups checkbox select
        final LoadableDetachableModel<List<UserGroup>> userGroupModel = new LoadableDetachableModel<List<UserGroup>>(){
            @Override
            protected List<UserGroup> load() {
                List<UserGroup> userGroupChoises = new ArrayList<UserGroup>();

                for (SecurityGroup securityGroup : SecurityGroup.values()){
                    boolean hasGroup = false;

                    for (UserGroup userGroup : userModel.getObject().getUserGroups()){
                        if (userGroup.getUserGroup().equals(securityGroup.name())){
                            userGroupChoises.add(userGroup);
                            hasGroup = true;
                            break;
                        }
                    }

                    if (!hasGroup){
                        UserGroup userGroup = new UserGroup();
                        userGroup.setUserGroup(securityGroup.name());
                        userGroupChoises.add(userGroup);
                    }
                }

                return userGroupChoises;
            }
        };

        form.add(new CheckBoxMultipleChoice<UserGroup>("usergroups",
                new PropertyModel<Collection<UserGroup>>(userModel, "userGroups"),
                userGroupModel, new IChoiceRenderer<UserGroup>(){
                    @Override
                    public Object getDisplayValue(UserGroup userGroup) {
                        return getString(userGroup.getUserGroup());
                    }

                    @Override
                    public String getIdValue(UserGroup userGroup, int index) {
                        return userGroup.getUserGroup();
                    }
                }));
    }

    /**
     * Деактивирование пользователя
     * @param login Логин пользователя
     * @return был ли пользователь авторизован
     */
    private boolean logout(String login){
        List<HttpSession> httpSessions = SecurityWebListener.getSessions(login);
        if (httpSessions.size() == 0) return false;

        for (HttpSession httpSession : httpSessions){
            getApplication().getSessionStore().unbind(httpSession.getId());
            httpSession.invalidate();
        }
        return true;
    }
}
