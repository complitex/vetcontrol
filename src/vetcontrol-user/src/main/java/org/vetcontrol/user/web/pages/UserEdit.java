package org.vetcontrol.user.web.pages;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.*;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.user.service.UserBean;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.security.SecurityWebListener;
import org.vetcontrol.web.template.FormTemplatePage;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.model.ResourceModel;

import static org.vetcontrol.entity.SecurityGroup.*;

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
        add(new Label("title", new ResourceModel("user.edit.title")));
        add(new Label("header", new ResourceModel("user.edit.title")));

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
                        getSession().error(getString("user.edit.contain_login"));
                        return;
                    }
                }else if(user.getChangePassword() != null && !user.getChangePassword().isEmpty()){
                    user.setPassword(DigestUtils.md5Hex(user.getChangePassword()));
                }

                try {
                    boolean toLogout = userBean.isUserAuthChanged(user);

                    userBean.save(user);
                    log.info("Пользователь сохранен: " + user);
                    getSession().info(getString("user.info.saved"));

                    if (toLogout){
                        if (logout(user.getLogin())){
                            log.info("Текущая сессия пользователя деактивирована: " + user);
                            getSession().info(getString("user.info.logoff"));
                        }
                    }
                } catch (Exception e) {
                    log.error("Ошибка сохранения пользователя в базу данных");
                    getSession().error(getString("user.info.error.saved"));
                }

                setResponsePage(UserList.class);
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

        //Job drop down menu
        List<Job> jobs = null;
        try {
            jobs = userBean.getJobs();
        } catch (Exception e) {
            log.error("Ошибка загрузки списка должностей",e);
        }
        DropDownChoice ddcJob = new DropDownChoice<Job>("user.job",
                new PropertyModel<Job>(userModel, "job"),
                jobs, new IChoiceRenderer<Job>() {
                    @Override
                    public Object getDisplayValue(Job job) {
                        return job.getDisplayName(getLocale(), localeDAO.systemLocale());
                    }

                    @Override
                    public String getIdValue(Job job, int index) {
                        return String.valueOf(job.getId());
                    }
                });
                
        form.add(ddcJob);

        //Department drop down menu
        List<Department> departments = null;
        try {
            departments = userBean.getDepartments();
        } catch (Exception e) {
            log.error("Ошибка загрузки списка структурных единиц",e);
        }
        DropDownChoice ddcDepartment = new DropDownChoice<Department>("user.department",
                new PropertyModel<Department>(userModel, "department"),
                departments, new IChoiceRenderer<Department>() {
                    @Override
                    public Object getDisplayValue(Department department) {
                        return department.getDisplayName(getLocale(), localeDAO.systemLocale());
                    }

                    @Override
                    public String getIdValue(Department department, int index) {
                        return String.valueOf(department.getId());
                    }
                });

        ddcDepartment.setRequired(true);

        form.add(ddcDepartment);

        //User groups checkbox select

        Map<SecurityGroup, IModel<UserGroup>> userGroupsMap = new HashMap<SecurityGroup, IModel<UserGroup>>();

        for (SecurityGroup securityGroup : SecurityGroup.values()){
            boolean hasGroup = false;

            for (UserGroup userGroup : userModel.getObject().getUserGroups()){
                if (userGroup.getSecurityGroup().equals(securityGroup)){
                    userGroupsMap.put(userGroup.getSecurityGroup(), new Model<UserGroup>(userGroup));
                    hasGroup = true;
                    break;
                }
            }

            if (!hasGroup){
                UserGroup userGroup = new UserGroup();
                userGroup.setSecurityGroup(securityGroup);
                userGroupsMap.put(userGroup.getSecurityGroup(), new Model<UserGroup>(userGroup));
            }
        }

        CheckGroup<UserGroup> usergroups = new CheckGroup<UserGroup>("usergroups",
                new PropertyModel<Collection<UserGroup>>(userModel, "userGroups"));
        
        usergroups.add(new Check<UserGroup>("ADMINISTRATORS", userGroupsMap.get(ADMINISTRATORS)));
        usergroups.add(new Check<UserGroup>("DEPARTMENT_OFFICERS", userGroupsMap.get(DEPARTMENT_OFFICERS)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS", userGroupsMap.get(LOCAL_OFFICERS)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_EDIT", userGroupsMap.get(LOCAL_OFFICERS_EDIT)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_VIEW", userGroupsMap.get(LOCAL_OFFICERS_DEP_VIEW)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_EDIT", userGroupsMap.get(LOCAL_OFFICERS_DEP_EDIT)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_CHILD_VIEW", userGroupsMap.get(LOCAL_OFFICERS_DEP_CHILD_VIEW)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_CHILD_EDIT", userGroupsMap.get(LOCAL_OFFICERS_DEP_CHILD_EDIT)));
        usergroups.add(new Check<UserGroup>("MOBILE_OFFICERS", userGroupsMap.get(MOBILE_OFFICERS)));

        form.add(usergroups);
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
