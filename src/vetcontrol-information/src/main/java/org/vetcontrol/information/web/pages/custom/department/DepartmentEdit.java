/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.pages.custom.department;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.entity.CustomsPoint;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.Log;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.information.service.dao.DepartmentDAO;
import org.vetcontrol.information.service.dao.IBookDAO;
import org.vetcontrol.information.util.web.BookTypeWebInfoUtil;
import org.vetcontrol.information.util.web.BookWebInfo;
import org.vetcontrol.information.util.web.CanEditUtil;
import org.vetcontrol.information.util.web.TruncateUtil;
import org.vetcontrol.information.web.component.BookChoiceRenderer;
import org.vetcontrol.information.web.component.edit.LocalizableTextPanel;
import org.vetcontrol.information.web.component.edit.SaveUpdateConfirmationDialog;
import org.vetcontrol.information.web.model.DisplayBookClassModel;
import org.vetcontrol.information.web.pages.BookPage;
import org.vetcontrol.service.LogBean;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.DateUtil;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.util.book.BookHash;
import org.vetcontrol.util.book.Property;
import org.vetcontrol.web.component.Spacer;
import org.vetcontrol.web.component.toolbar.DisableItemButton;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.security.SecurityRoles;
import org.vetcontrol.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRoles.INFORMATION_VIEW)
public final class DepartmentEdit extends FormTemplatePage {

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;
    @EJB(name = "BookDAO")
    IBookDAO bookDAO;
    @EJB(name = "LogBean")
    private LogBean logBean;
    @EJB(name = "InformationDepartmentDAO")
    private DepartmentDAO departmentDAO;
    private static final Logger log = LoggerFactory.getLogger(DepartmentEdit.class);
    private static final String PASSING_BORDER_POINT_NAME_REQUIRED = "passing_border_point_name";
    private Department department;

    public DepartmentEdit() {
        init();
    }

    private void init() {
        final Locale systemLocale = localeDAO.systemLocale();

        department = (Department) getSession().getMetaData(BookPage.SELECTED_BOOK_ENTRY);
        if (department == null) {
            throw new IllegalArgumentException("selected book entry may not be null");
        }
        bookDAO.addLocalizationSupport(department);
        BeanPropertyUtil.addLocalization(department, localeDAO.all());
        departmentDAO.loadPassingBorderPoints(department);

        //calculate initial hash code for book entry in order to increment version of the book entry if necessary later.
        final BookHash initial = BeanPropertyUtil.hash(department);

        //title
        add(new Label("title", new DisplayBookClassModel(Department.class)));
        add(new Label("caption", new DisplayBookClassModel(Department.class)));

        //messages
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        //form
        final Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        //department name
        form.add(new LocalizableTextPanel("name",
                new PropertyModel(department, "names"),
                BeanPropertyUtil.getPropertyByName(Department.class, "names"),
                systemLocale, CanEditUtil.canEdit(department)));

        //parent department
        IModel<List<Department>> parentDepartmentsModel = new LoadableDetachableModel<List<Department>>() {

            @Override
            protected List<Department> load() {
                return departmentDAO.getAvailableDepartments(department);
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
                }
            }

            @Override
            public void detach() {
            }
        };
        BookChoiceRenderer parentRenderer = new BookChoiceRenderer(BeanPropertyUtil.getPropertyByName(Department.class, "parent"), systemLocale,
                TruncateUtil.TRUNCATE_SELECT_VALUE_IN_EDIT_PAGE);
        final DropDownChoice parent = new DropDownChoice("parent",
                parentModel,
                parentDepartmentsModel,
                parentRenderer);
        parent.setOutputMarkupId(true);
        parent.setEnabled(BeanPropertyUtil.isNewBook(department) && CanEditUtil.canEdit(department));
        form.add(parent);

        //customs point
        final WebMarkupContainer customsPointMessageZone = new WebMarkupContainer("customsPointMessageZone");
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
        BookChoiceRenderer customsPointRenderer = new BookChoiceRenderer(BeanPropertyUtil.getPropertyByName(Department.class, "customsPoint"),
                systemLocale, TruncateUtil.TRUNCATE_SELECT_VALUE_IN_EDIT_PAGE);
        final DropDownChoice customsPoint = new DropDownChoice("customsPoint",
                new PropertyModel(department, "customsPoint"),
                customsPointModel,
                customsPointRenderer);
        customsPoint.setEnabled(CanEditUtil.canEdit(department));
        customsPointSelectZone.add(customsPoint);

        //passing border points
        final WebMarkupContainer passingBorderPointsSection = new WebMarkupContainer("passingBorderPointsSection");
        passingBorderPointsSection.setOutputMarkupId(true);

        final AjaxLink addPassingBorderPoint = new AjaxLink("addPassingBorderPoint") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                department.addPassingBorderPoint(new PassingBorderPoint());
                target.addComponent(passingBorderPointsSection);
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
                //disable old entry.
                disableDepartment();

                //save new entry.
                BeanPropertyUtil.clearBook(department);
                for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
                    BeanPropertyUtil.clearBook(borderPoint);
                    borderPoint.setNeedToUpdate(true);
                }
                saveOrUpdate(initial);
                goToListPage();
            }
        };
        add(confirmationDialog);
        //save and cancel links.
        AjaxSubmitLink save = new AjaxSubmitLink("save") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (BeanPropertyUtil.isNewBook(department)) {
                    //new entry
                    saveOrUpdate(initial);
                    goToListPage();
                } else {
                    confirmationDialog.open(target);
                }

                target.addComponent(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        };
        save.setVisible(CanEditUtil.canEdit(department));
        form.add(save);

        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                goToListPage();
            }
        };
        cancel.setVisible(CanEditUtil.canEdit(department));
        form.add(cancel);

        Link back = new Link("back") {

            @Override
            public void onClick() {
                goToListPage();
            }
        };
        back.setVisible(!CanEditUtil.canEdit(department));
        form.add(back);

        form.add(new Spacer("spacer"));
    }

    private void goToListPage() {
        BookWebInfo departmentWebInfo = BookTypeWebInfoUtil.getInfo(Department.class);
        setResponsePage(departmentWebInfo.getListPage(), departmentWebInfo.getListPageParameters());
    }

    private void saveOrUpdate(BookHash initial) {
        Long id = department.getId();
        Log.EVENT event = id == null ? Log.EVENT.CREATE : Log.EVENT.EDIT;

        //update version of book and its localizable strings if necessary.
        BeanPropertyUtil.updateVersionIfNecessary(department, initial);
        updateDepartmentReferences(department);

        try {
            departmentDAO.saveOrUpdate(department);
            logBean.info(Log.MODULE.INFORMATION, event, DepartmentEdit.class, Department.class, "ID: " + id);
        } catch (Exception e) {
            log.error("Ошибка сохранения справочника", e);
            logBean.error(Log.MODULE.INFORMATION, event, DepartmentEdit.class, Department.class, "ID: " + id);
        }
    }

    private void disableDepartment() {
        bookDAO.disable(department);
        disableReferences();
        logBean.info(Log.MODULE.INFORMATION, Log.EVENT.DISABLE, DepartmentEdit.class, Department.class, "ID: " + department.getId());
    }

    private void updateDepartmentReferences(Department department) {
        Date newVersion = DateUtil.getCurrentDate();
        for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
            if (borderPoint.isNeedToUpdate()) {
                borderPoint.setUpdated(newVersion);
            }
        }
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = new ArrayList<ToolbarButton>();
        toolbarButtons.add(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                disableDepartment();
                goToListPage();
            }

            @Override
            protected void onBeforeRender() {
                if (BeanPropertyUtil.isNewBook(department) || !CanEditUtil.canEdit(department)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
        return toolbarButtons;
    }

    private void disableReferences() {
        for (PassingBorderPoint borderPoint : department.getPassingBorderPoints()) {
            if (!BeanPropertyUtil.isNewBook(borderPoint)) {
                bookDAO.disable(borderPoint);
            }
        }
    }

    private class PassingBorderPointListItem extends ListItem<PassingBorderPoint> {

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
                    //log.info("name = " + object);
                    boolean isTheSameName = isStringsEqual(object, getModelObject().getName());
                    if (!isTheSameName) {
                        getModelObject().setName(object);
                        getModelObject().setNeedToUpdate(true);
                    }
                }

                @Override
                public void detach() {
                }
            };
            final TextField<String> passingBorderPoint = new TextField<String>("passingBorderPointName", passingBorderPointModel) {

                @Override
                public String getValidatorKeyPrefix() {
                    return PASSING_BORDER_POINT_NAME_REQUIRED;
                }
            };
            Property nameProp = BeanPropertyUtil.getPropertyByName(PassingBorderPoint.class, "name");
            passingBorderPoint.add(new SimpleAttributeModifier("size", String.valueOf(nameProp.getLength())));
            passingBorderPoint.add(new SimpleAttributeModifier("maxlength", String.valueOf(nameProp.getLength())));
            passingBorderPoint.setRequired(true);
            passingBorderPoint.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.addComponent(passingBorderPoint);
                }
            });

            passingBorderPoint.setEnabled(CanEditUtil.canEdit(department));
            add(passingBorderPoint);

            //activate link
            AjaxLink activate = new AjaxLink("activate") {

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
            AjaxLink deactivate = new AjaxLink("deactivate") {

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

    private static boolean isStringsEqual(String a, String b) {
        if (a == b) {
            return true;
        }

        String strippedA = null;
        if (a != null) {
            strippedA = a.trim();
        }

        String strippedB = null;
        if (b != null) {
            strippedB = b.trim();
        }

        if (strippedA != null && strippedB != null && strippedA.equals(strippedB)) {
            return true;
        }
        return false;
    }
}
    

