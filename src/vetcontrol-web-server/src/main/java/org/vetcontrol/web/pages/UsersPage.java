/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages;

import java.util.List;
import javax.ejb.EJB;
import org.vetcontrol.model.AllRoles;
import org.vetcontrol.model.beans.Role;
import org.vetcontrol.model.beans.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.vetcontrol.service.fasade.pages.UsersPageFasade;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation({AllRoles.DONT_EDIT_ADMIN, AllRoles.ADMIN})
public class UsersPage extends BasePage {

    @EJB(name="UsersPageFasade")
    private UsersPageFasade fasade;

    public UsersPage() {
        add(new Label("message", "All Users"));

        List<User> users = fasade.getAll();
        
        //add user list
        add(new ListView<User>("users", users) {

            @Override
            protected void populateItem(ListItem<User> item) {
                User u = item.getModelObject();
                item.add(new Label("name", u.getName()));
                item.add(new Label("blocked", u.isBlocked() ? "Yes" : "No"));
                PageParameters params = new PageParameters();
                params.add("name", u.getName());
                final BookmarkablePageLink edit = new BookmarkablePageLink("edit", AddUpdateUserPage.class, params);
                MetaDataRoleAuthorizationStrategy.authorize(edit, ENABLE, AllRoles.ADMIN);
                item.add(edit);

                String rolesList = "";
                int i = 0;
                for (Role role : u.getRoles()) {
                    if (i > 0) {
                        rolesList += ", ";
                    }
                    rolesList += role.getId().getRole();
                    i++;
                }
                Label roles = new Label("roles", rolesList);
                item.add(roles);

            }
        });

        //add newUser
        final BookmarkablePageLink newUser = new BookmarkablePageLink("newUser", AddUpdateUserPage.class);
        MetaDataRoleAuthorizationStrategy.authorize(newUser, RENDER, AllRoles.ADMIN);
        add(newUser);
    }
}
