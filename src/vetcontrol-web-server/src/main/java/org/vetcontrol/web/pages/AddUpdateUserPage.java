/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages;

import org.vetcontrol.service.dao.users.SuchRoleDoesNotExistException;
import org.vetcontrol.service.fasade.pages.AddUpdateUserPageFasade;
import java.io.Serializable;
import java.util.Iterator;
import javax.ejb.EJB;
import org.vetcontrol.model.AllRoles;
import org.vetcontrol.model.beans.Role;
import org.vetcontrol.model.beans.RoleId;
import org.vetcontrol.model.beans.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(AllRoles.ADMIN)
public class AddUpdateUserPage extends BasePage {

    @EJB(name = "AddUpdateUserPageFasade")
    private AddUpdateUserPageFasade fasade;

    public AddUpdateUserPage() {
        System.out.println("AddUpdateUserPage ctor");
        init(new User());
    }

    public void init(final User user) {
        //messages
        add(new FeedbackPanel("messages"));

        //form
        Form form = new Form("form");
        add(form);
        //add userName label
        add(new Label("userNameLabel", user.getName()));

        //user characteristics
        form.add(new TextField("name", new PropertyModel<String>(user, "name")));
        form.add(new PasswordTextField("password", new PropertyModel(user, "password")).setResetPassword(false));
        form.add(new CheckBox("blocked", new PropertyModel<Boolean>(user, "blocked")));

        //roles

        IDataProvider provider = new IDataProvider() {

            @Override
            public Iterator iterator(int first, int count) {
                return user.getRoles().iterator();
            }

            @Override
            public int size() {
                return user.getRoles().size();
            }

            @Override
            public void detach() {
            }

            @Override
            public IModel model(Object o) {
                return new Model((Serializable) o);
            }
        };

        form.add(new DataView<Role>("roles", provider) {

            @Override
            protected void populateItem(Item<Role> item) {
                Role role = item.getModelObject();
                final Label roleOut = new Label("roleOut", new PropertyModel(role, "id.role"));
                final TextField roleIn = new TextField("roleIn", new PropertyModel(role, "id.role"));
                item.add(roleOut);
                item.add(roleIn);
                if (!isBlank(role.getId().getRole())) {
                    roleIn.setVisible(false);
                } else {
                    roleOut.setVisible(false);
                }

                item.add(new SubmitLink("removeRole", new Model<Role>(role)) {

                    @Override
                    public void onSubmit() {
                        for (Iterator<Role> it = user.getRoles().iterator(); it.hasNext();) {
                            Role r = it.next();

                            if (r.getId().getRole() == null || r.getId().getRole().equals(((Role) getDefaultModelObject()).getId().getRole())) {
                                it.remove();
                            }
                        }
                    }
                });
            }
        });

        //add role
        form.add(new SubmitLink("addRole") {

            @Override
            public void onSubmit() {
                RoleId id = new RoleId("", user.getName());
                user.getRoles().add(new Role(id, user));
            }
        });

        //cancel
        form.add(new SubmitLink("cancel") {

            @Override
            public void onSubmit() {
                setRedirect(true);
                setResponsePage(UsersPage.class);
            }
        }.setDefaultFormProcessing(false));

        //save/update
        form.add(new SubmitLink("saveOrUpdate") {

            @Override
            public void onSubmit() {
                try {
                    fasade.saveOrUpdate(user);
                    setResponsePage(UsersPage.class);
                } catch (SuchRoleDoesNotExistException e) {
                    error("Such role (" + e.getRole() + ") does not exist.");
                }
            }
        });
    }

    public AddUpdateUserPage(PageParameters params) {
        init(fasade.getUser(params.getString("name")));
    }

    private static boolean isBlank(String value) {
        return value == null || value.equals("");
    }
}
