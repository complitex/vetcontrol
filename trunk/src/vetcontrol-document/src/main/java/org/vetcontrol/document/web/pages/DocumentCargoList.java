package org.vetcontrol.document.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.vetcontrol.document.service.DocumentBean;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.IBook;
import org.vetcontrol.service.dao.ILocaleDAO;
import org.vetcontrol.util.book.BeanPropertyUtil;
import org.vetcontrol.web.component.BookmarkablePageLinkPanel;
import org.vetcontrol.web.component.toolbar.ToolbarButton;
import org.vetcontrol.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.vetcontrol.web.component.toolbar.AddDocumentButton;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 12:54:13
 */
public class DocumentCargoList extends TemplatePage{
    private final static int ITEMS_ON_PAGE = 13;

    @EJB(name = "LocaleDAO")
    private ILocaleDAO localeDAO;


    @EJB(name = "DocumentBean")
    DocumentBean documentBean;

    public DocumentCargoList() {
        super();

        add(new Label("title", getString("document.cargo.list.title")));

        final SortableDataProvider<DocumentCargo> dataProvider = new SortableDataProvider<DocumentCargo>(){

            @Override
            public Iterator<? extends DocumentCargo> iterator(int first, int count) {
                return documentBean.getDocumentCargos().iterator();
            }

            @Override
            public int size() {
                return documentBean.getDocumentCargosSize().intValue();
            }

            @Override
            public IModel<DocumentCargo> model(DocumentCargo object) {
                return new Model<DocumentCargo>(object);
            }
        };

        List<IColumn<DocumentCargo>> columns = new ArrayList<IColumn<DocumentCargo>>();
        columns.add(new PropertyColumn<DocumentCargo>(new Model<String>(getString("document.cargo.id")), "id"));
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.movement_type"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, getName(rowModel.getObject().getMovementType())));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.vehicle_type"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, getName(rowModel.getObject().getVehicleType())));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.vehicle_details"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getVehicleDetails()));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.cargo_count"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, String.valueOf(rowModel.getObject().getCargos().size())));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.cargo_sender"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, getName(rowModel.getObject().getCargoSender())));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.cargo_receiver"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, getName(rowModel.getObject().getCargoReceiver())));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(new Model<String>(getString("document.cargo.producer"))){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {
                cellItem.add(new Label(componentId, getName(rowModel.getObject().getProducer())));
            }
        });
        columns.add(new AbstractColumn<DocumentCargo>(null){
            @Override
            public void populateItem(Item<ICellPopulator<DocumentCargo>> cellItem, String componentId, IModel<DocumentCargo> rowModel) {

                cellItem.add(new BookmarkablePageLinkPanel<DocumentCargo>(componentId, "Редактировать", DocumentCargoEdit.class,  
                        new PageParameters("doc_cargo_id=" + rowModel.getObject().getId())));
            }
        });


        DefaultDataTable<DocumentCargo> dataTable = new DefaultDataTable<DocumentCargo>("document.cargo.list.table", columns, dataProvider, ITEMS_ON_PAGE);

        add(dataTable);

    }

    private String getName(IBook iBook){
        return BeanPropertyUtil.getLocalizablePropertyAsString(iBook.getNames(), localeDAO.systemLocale(), null);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList( (ToolbarButton) new AddDocumentButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(DocumentCargoEdit.class);
            }
        });
    }

}
