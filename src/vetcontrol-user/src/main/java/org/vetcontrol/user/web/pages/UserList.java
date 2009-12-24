package org.vetcontrol.user.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.UserGroup;
import org.vetcontrol.user.service.UserBean;
import org.vetcontrol.web.security.SecurityRoles;

import javax.ejb.EJB;
import java.util.Iterator;
import java.util.List;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 21:23:07
 */
@AuthorizeInstantiation(SecurityRoles.USER_EDIT)
public class UserList extends UserI18N{
    @EJB(name = "UserBean")
    private UserBean userBean;

    public UserList() {
        super();

        add(new Label("title", getString("user.list.title")));

        List<User> users = userBean.getUsers();

        add(new ListView<User>("users", users){
            @Override
            protected void populateItem(ListItem listItem) {
                User user = (User) listItem.getModelObject();
                listItem.add(new Label("last_name", user.getLastName()));
                listItem.add(new Label("first_name", user.getFirstName()));
                listItem.add(new Label("middle_name", user.getMiddleName()));
                if (user.getDepartment() != null){
                    listItem.add(new Label("department", user.getDepartment().getName()));
                }else{
                    listItem.add(new Label("department", "DEVELOPMENT"));                    
                }
                listItem.add(new Label("login", user.getLogin()));
                listItem.add(new Label("groups", getGroups(user)));
                listItem.add(new BookmarkablePageLink<UserEdit>("edit", UserEdit.class,
                        new PageParameters("user_id="+user.getId())));
            }
        });
    }

    private String getGroups(User user){
        if (user.getUserGroups() == null || user.getUserGroups().isEmpty()){
            return getString("user.blocked");
        }

        StringBuilder sb = new StringBuilder();

        for (Iterator<UserGroup> it = user.getUserGroups().iterator();;){
            sb.append(getString(it.next().getUserGroup()));
            if (!it.hasNext()) return sb.toString();
            sb.append(", ");
        }
    }

}
