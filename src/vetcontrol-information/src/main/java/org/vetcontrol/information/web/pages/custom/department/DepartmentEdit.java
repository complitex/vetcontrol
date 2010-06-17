/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages.custom.department;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.CustomsPoint;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.information.service.dao.DepartmentBookDAO;
import org.vetcontrol.information.web.util.CanEditUtil;
import org.vetcontrol.information.web.util.TruncateUtil;
import org.vetcontrol.information.web.component.BookChoiceRenderer;
import org.vetcontrol.web.component.book.DisableAwareDropDownChoice;
import org.vetcontrol.information.web.component.edit.LocalizableTextPanel;
import org.vetcontrol.information.web.component.edit.SaveUpdateConfirmationDialog;
import org.vetcontrol.information.web.pages.BookPage;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import static org.vetcontrol.book.BeanPropertyUtil.*;
import org.vetcontrol.book.BookHash;
import org.vetcontrol.book.Property;
import org.vetcontrol.entity.Change;
import org.vetcontrol.information.web.component.edit.GoToListPagePanel;
import org.vetcontrol.information.web.model.DisplayPropertyLocalizableModel;
import org.vetcontrol.information.util.change.BookChangeManager;
import org.vetcontrol.util.CloneUtil;
import org.vetcontrol.information.util.resource.CommonResourceKeys;
import org.vetcontrol.information.web.util.PageManager;
import org.vetcontrol.service.dao.IBookViewDAO;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.component.toolbar.DisableItemButton;
import org.vetcontrol.web.component.toolbar.EnableItemButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public final class DepartmentEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(DepartmentEdit.class);

    private static final String PASSING_BORDER_POINT_NAME_REQUIRED = "passing_border_point_name";

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;

    @EJB(name = "BookViewDAO")
    private IBookViewDAO bookViewDAO;

    @EJB(name = "LogBean")
    private LogBean logBean;

    @EJB(name = "DepartmentBookDAO")
    private DepartmentBookDAO departmentDAO;

    private Department department;

    private Department oldDepartment;

    public DepartmentEdit() {
        init();
    }

    private void init() {
        final Locale systemLocale = getSystemLocale();
        final List<Locale> allLocales = localeDAO.all();

        department = (Department) getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);
        if (department == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }
        bookViewDAO.addLocalizationSupport(department);
        addLocalization(department, allLocales);
        departmentDAO.loadPassingBorderPoints(department);

        oldDepartment = CloneUtil.cloneObject(department);

        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = hash(department);

        //title
        add(new Label("title", new ResourceModel("page.title")));

        //messages
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        //form
        final Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        //department name
        form.add(new Label("nameLabel", new DisplayPropertyLocalizableModel(getPropertyByName(Department.class, "names"))));

        form.add(new LocalizableTextPanel("name",
                new PropertyModel(department, "names"),
                getPropertyByName(Department.class, "names"),
                systemLocale, CanEditUtil.canEdit(department)));

        //parent department
        form.add(new Label("parentLabel", new DisplayPropertyLocalizableModel(getPropertyByName(Department.class, "parent"))));

        IModel<List<Department>> parentDepartmentsModel = new LoadableDetachableModel<List<Department>>() {

            @Override
            protected List<Department> load() {
                return departmentDAO.getAvailableDepartments(department.getId());
            }
        };
        IModel<Department> parentModel = new IModel<Department>() {

            @Override
            public Department getObject() {
                return department.getParent();
            }

            @Override
            public void setObject(Department parent) {
                if (parent != null) {
                    department.setParent(parent);
                    department.setLevel(parent.getLevel() + 1);
                } else {
                    department.setLevel(1);
                }
            }

            @Override
            public void detach() {
            }
        };
        BookChoiceRenderer parentRenderer = new BookChoiceRenderer(getPropertyByName(Department.class, "parent"), systemLocale,
                TruncateUtil.TRUNCATE_SELECT_VALUE_IN_EDIT_PAGE);
        final DropDownChoice<Department> parent = new DisableAwareDropDownChoice<Department>("parent",
                parentModel,
                parentDepartmentsModel,
                parentRenderer);
        parent.setOutputMarkupId(true);
        parent.setEnabled(isNewBook(department) && CanEditUtil.canEdit(department));
        form.add(parent);

        //customs point
        final WebMarkupContainer customsPointMessageZone = new WebMarkupContainer("customsPointMessageZone");
        customsPointMessageZone.add(new Label("customsPointLabel",
                new DisplayPropertyLocalizableModel(getPropertyByName(Department.class, "customsPoint"))));
        final WebMarkupContainer customsPointSelectZone = new WebMarkupContainer("customsPointSelectZone");

        final WebMarkupContainer customsPointZone = new WebMarkupContainer("customsPointZone") {

            @Override
            protected void onBeforeRender() {
                boolean isVisible = department.getLevel() != null && department.getLevel() == 2;
                customsPointMessageZone.setVisible(isVisible);
                customsPointSelectZone.setVisible(isVisible);
                super.onBeforeRender();
            }
        };
        customsPointZone.setOutputMarkupId(true);
        customsPointZone.add(customsPointMessageZone);
        customsPointZone.add(customsPointSelectZone);
        form.add(customsPointZone);

        IModel<List<CustomsPoint>> customsPointModel = new LoadableDetachableModel<List<CustomsPoint>>() {

            @Override
            protected List<CustomsPoint> load() {
                return departmentDAO.getAvailableCustomsPoint();
            }
        };
        BookChoiceRenderer customsPointRenderer = new BookChoiceRenderer(getPropertyByName(Department.class, "customsPoint"),
                systemLocale, TruncateUtil.TRUNCATE_SELECT_VALUE_IN_EDIT_PAGE);
        final DropDownChoice<CustomsPoint> customsPoint = new DisableAwareDropDownChoice<CustomsPoint>("customsPoint",
                new PropertyModel(department, "customsPoint"),
                customsPointModel,
                customsPointRenderer);
        customsPoint.setEnabled(CanEditUtil.canEdit(department));
        customsPointSelectZone.add(customsPoint);

        //passing border points
        final WebMarkupContainer passingBorderPointsSection = new WebMarkupContainer("passingBorderPointsSection");
        passingBorderPointsSection.setOutputMarkupId(true);

        passingBorderPointsSection.add(new Label("passingBorderPointNameLabel",
                new DisplayPropertyLocalizableModel(getPropertyByName(PassingBorderPoint.class, "name"))));

        final AjaxLink addPassingBorderPoint = new AjaxLink("addPassingBorderPoint") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                department.addPassingBorderPoint(new PassingBorderPoint());
                target.addComponent(passingBorderPointsSection);

                String setFocusOnNewBorderPoint = "newBorderPointInputId = $('#passingBorderPoints tbody tr:last input[type=\"text\"]:first').attr('id');"
                        + "Wicket.Focus.setFocusOnId(newBorderPointInputId);";
                target.appendJavascript(setFocusOnNewBorderPoint);
            }
        };
        addPassingBorderPoint.setVisible(CanEditUtil.canEdit(department));
        passingBorderPointsSection.add(addPassingBorderPoint);

        //passing border points list view
        ListView<PassingBorderPoint> passingBorderPoints = new ListView<PassingBorderPoint>("passingBorderPoints",
                department.getPassingBorderPoints()) {

            @Override
            protected void populateItem(ListItem<PassingBorderPoint> item) {
            }

            @Override
            protected ListItem<PassingBorderPoint> newItem(int index) {
                return new PassingBorderPointListItem(index, getListItemModel(getModel(), index));
            }

            @Override
            protected IModel<PassingBorderPoint> getListItemModel(IModel<? extends List<PassingBorderPoint>> listViewModel, int index) {
                return new Model<PassingBorderPoint>(listViewModel.getObject().get(index));
            }
        };
        passingBorderPoints.setReuseItems(true);
        passingBorderPointsSection.add(passingBorderPoints);

        final WebMarkupContainer passingBorderPointsZone = new WebMarkupContainer("passingBorderPointsZone") {

            @Override
            protected void onBeforeRender() {
                boolean isVisible = department.getLevel() != null && department.getLevel() == 3;
                passingBorderPointsSection.setVisible(isVisible);
                super.onBeforeRender();
            }
        };
        passingBorderPointsZone.setOutputMarkupId(true);
        passingBorderPointsZone.add(passingBorderPointsSection);
        form.add(passingBorderPointsZone);

        parent.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (department.getParent() != null) {
                    parent.setEnabled(false);
                }
                target.addComponent(parent);
                target.addComponent(customsPointZone);
                target.addComponent(passingBorderPointsZone);
            }
        });

        final SaveUpdateConfirmationDialog confirmationDialog = new SaveUpdateConfirmationDialog("confirmationDialogPanel") {

            @Override
            public void update() {
                saveOrUpdate(initial);
                goToListPage();
            }

            @Override
            public void createNew() {
                saveAsNew();
                goToListPage();
            }
        };
        add(confirmationDialog);
        //save and cancel links.
        AjaxSubmitLink save = new AjaxSubmitLink("save") {

            private boolean validated;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (validate()) {
                    if (isNewBook(department)) {
                        //new entry
                        saveOrUpdate(initial);
                        goToListPage();
                    } else {
                        confirmationDialog.open(target);
                    }
                    target.addComponent(form);
                } else {
                    target.addComponent(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                validate();
                target.addComponent(messages);
                validated = false;
            }

            private boolean validate() {
                if (!validated) {
                    validated = true;
                    for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
                        if (Strings.isEmpty(borderPoint.getName())) {
                            error(getString(PASSING_BORDER_POINT_NAME_REQUIRED));
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        save.setVisible(CanEditUtil.canEdit(department));
        form.add(save);
        form.add(new GoToListPagePanel("goToListPagePanel", department));
        form.add(new Spacer("spacer"));
    }

    private void goToListPage() {
        PageManager.goToListPage(this, Department.class);
    }

    private void saveOrUpdate(BookHash initial) {
        Log.EVENT event = isNewBook(department) ? Log.EVENT.CREATE : Log.EVENT.EDIT;

        Set<Change> changes = null;
        if (event == Log.EVENT.EDIT) {
            try {
                changes = BookChangeManager.getChanges(oldDepartment, department, getSystemLocale());

                if (log.isDebugEnabled()) {
                    for (Change change : changes) {
                        log.debug(change.toString());
                    }
                }
            } catch (Exception e) {
                log.error("Error with getting changes for " + Department.class.getName() + " book entry.", e);
            }
        }

        Long oldId = department.getId();

        //update version of book and its localizable strings if necessary.
        updateVersionIfNecessary(department, initial);
        departmentDAO.updateReferences(department);

        try {
            departmentDAO.saveOrUpdate(department, null);
            Long newId = department.getId();
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_UPDATE_KEY, this, null, new Object[]{newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, event, DepartmentEdit.class, Department.class, message, changes);
        } catch (Exception e) {
            log.error("Error with saving the book.", e);
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_UPDATE_KEY, this, null, new Object[]{oldId}).getObject();
            logBean.error(Log.MODULE.INFORMATION, event, DepartmentEdit.class, Department.class, message);
        }
    }

    private void saveAsNew() {
        Long oldId = department.getId();
        try {
            departmentDAO.saveAsNew(department);
            Long newId = department.getId();
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_AS_NEW_KEY, this, null, new Object[]{oldId, newId}).getObject();
            logBean.info(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, DepartmentEdit.class, Department.class, message);
        } catch (Exception e) {
            log.error("Error with saving the book.", e);
            String message = new StringResourceModel(CommonResourceKeys.LOG_SAVE_AS_NEW_KEY, this, null, new Object[]{oldId, null}).getObject();
            logBean.error(Log.MODULE.INFORMATION, Log.EVENT.CREATE_AS_NEW, DepartmentEdit.class, Department.class, message);
        }
    }

    private void disableDepartment(Long departmentId) {
        departmentDAO.disable(departmentId);
        String message = new StringResourceModel(CommonResourceKeys.LOG_ENABLE_DISABLE_KEY, this, null, new Object[]{departmentId}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, DepartmentEdit.class, Department.class, message);
    }

    private void enableDepartment(Long departmentId) {
        departmentDAO.enable(departmentId);
        String message = new StringResourceModel(CommonResourceKeys.LOG_ENABLE_DISABLE_KEY, this, null, new Object[]{departmentId}).getObject();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.ENABLE, DepartmentEdit.class, Department.class, message);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableDepartment(department.getId());
                goToListPage();
            }

            @Override
            protected void onBeforeRender() {
                if (isNewBook(department) || !CanEditUtil.canEdit(department)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        toolbarButtons.add(new EnableItemButton(id) {

            @Override
            protected void onClick() {
                enableDepartment(department.getId());
                goToListPage();
            }

            @Override
            protected void onBeforeRender() {
                if (isNewBook(department) || !CanEditUtil.canEditDisabled(department)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }

    private class PassingBorderPointListItem extends ListItem<PassingBorderPoint> {

        private TextField<String> passingBorderPoint;

        private AjaxLink activate;

        private AjaxLink deactivate;

        public PassingBorderPointListItem(int index, IModel<PassingBorderPoint> model) {
            super(index, model);
            setOutputMarkupId(true);
            init();
        }

        private void init() {
            //passing border point.
            IModel<String> passingBorderPointModel = new IModel<String>() {

                @Override
                public String getObject() {
                    return getModelObject().getName();
                }

                @Override
                public void setObject(String object) {
//                    log.info("name = " + object);
                    boolean isTheSameName = areStringsEqual(object, getModelObject().getName());
                    if (!isTheSameName) {
                        getModelObject().setName(object);
                        getModelObject().setNeedToUpdate(true);
                    }
                }

                @Override
                public void detach() {
                }
            };
            passingBorderPoint = new TextField<String>("passingBorderPointName", passingBorderPointModel) {

                @Override
                public boolean isEnabled() {
                    return CanEditUtil.canEdit(department) && !PassingBorderPointListItem.this.getModelObject().isDisabled();
                }
            };
            Property nameProperty = getPropertyByName(PassingBorderPoint.class, "name");
            passingBorderPoint.add(new SimpleAttributeModifier("size", String.valueOf(nameProperty.getLength())));
            passingBorderPoint.add(new SimpleAttributeModifier("maxlength", String.valueOf(nameProperty.getLength())));
            passingBorderPoint.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(passingBorderPoint);

            //activate link
            activate = new AjaxLink("activate") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    PassingBorderPointListItem.this.getModelObject().setDisabled(false);
                    PassingBorderPointListItem.this.getModelObject().setNeedToUpdate(true);
                    target.addComponent(PassingBorderPointListItem.this);
                }

                @Override
                public boolean isEnabled() {
                    return PassingBorderPointListItem.this.getModelObject().isDisabled();
                }
            };
            activate.setVisible(CanEditUtil.canEdit(department));
            add(activate);

            //deactivate link
            deactivate = new AjaxLink("deactivate") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    PassingBorderPointListItem.this.getModelObject().setDisabled(true);
                    PassingBorderPointListItem.this.getModelObject().setNeedToUpdate(true);
                    target.addComponent(PassingBorderPointListItem.this);
                }

                @Override
                public boolean isEnabled() {
                    return !PassingBorderPointListItem.this.getModelObject().isDisabled();
                }
            };
            deactivate.setVisible(CanEditUtil.canEdit(department));
            add(deactivate);
        }
    }

    private static boolean areStringsEqual(String string1, String string2) {
        if ((string1 == null) && (string2 == null)) {
            return true;
        }
        if (Strings.isEmpty(string1) && Strings.isEmpty(string2)) {
            return true;
        }
        if (string1 == null || string2 == null) {
            return false;
        }
        return string1.trim().equals(string2.trim());
    }
}
    

