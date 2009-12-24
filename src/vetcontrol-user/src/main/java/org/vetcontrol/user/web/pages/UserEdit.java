package org.vetcontrol.user.web.pages;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.user.service.UserBean;
import org.vetcontrol.web.security.SecurityGroup;
import org.vetcontrol.web.security.SecurityRoles;
import sun.security.provider.MD5;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 22:26:17
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UserEdit extends UserI18N {
    @EJB(name = "UserBean")
    private UserBean userBean;

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

        LoadableDetachableModel<User> userModel = new LoadableDetachableModel<User>(){
            @Override
            protected User load() {
                return (id != null) ? userBean.getUser(id) : new User();
            }
        };

        Form form = new Form<User>("user_edit_form", userModel){
            @Override
            protected void onSubmit() {
                User user = getModelObject();

                //MD5 Password
                if (id == null){
                    user.setPassword(DigestUtils.md5Hex(user.getLogin()));
                    if (userBean.containsLogin(user.getLogin())){
                        error(getString("user.edit.contain_login"));
                        return;
                    }
                }else if(user.getChangePassword() != null && !user.getChangePassword().isEmpty()){
                    user.setPassword(DigestUtils.md5Hex(user.getChangePassword()));
                }

                userBean.save(user);
                info(getString("user.info.saved"));
            }
        };
        add(form);

        TextField login = new TextField<String>("login", new PropertyModel<String>(userModel, "login"));
        login.setEnabled(id == null);
        form.add(login);

        PasswordTextField password = new PasswordTextField("password", new PropertyModel<String>(userModel, "changePassword"));
        password.setEnabled(id != null);
        password.setRequired(false);
        form.add(password);

        form.add(new RequiredTextField<String>("last_name", new PropertyModel<String>(userModel, "lastName")));
        form.add(new RequiredTextField<String>("first_name", new PropertyModel<String>(userModel, "firstName")));
        form.add(new RequiredTextField<String>("middle_name", new PropertyModel<String>(userModel, "middleName")));

        //Department drop down menu
        List<Department> departments = userBean.getDepartments();
        form.add(new DropDownChoice<Department>("department", new PropertyModel<Department>(userModel, "department"),
                departments, new IChoiceRenderer<Department>() {
                    @Override
                    public Object getDisplayValue(Department department) {
                        //TODO Localize
                        return department.getName();
                    }

                    @Override
                    public String getIdValue(Department department, int index) {
                        return String.valueOf(department.getId());
                    }
                }));


        //User groups checkbox select    
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

        form.add(new CheckBoxMultipleChoice<UserGroup>("usergroups",
                new PropertyModel<Collection<UserGroup>>(userModel, "userGroups"),
                userGroupChoises, new IChoiceRenderer<UserGroup>(){
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
}
