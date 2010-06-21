/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.logging.web.component.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;
import org.vetcontrol.entity.Change;

/**
 *
 * @author Artem
 */
public final class DetailsPanel extends Panel {

    private static final Comparator<Change> PROPERTY_COMPARATOR = new Comparator<Change>() {

        @Override
        public int compare(Change o1, Change o2) {
            if (o1.getLocale() != null) {
                if (o2.getLocale() != null) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        }
    };

    public DetailsPanel(String id, Collection<Change> changes) {
        super(id);
        init(changes);
    }

    private void init(Collection<Change> changes) {
        add(CSSPackageResource.getHeaderContribution(DetailsPanel.class, DetailsPanel.class.getSimpleName() + ".css"));
        add(JavascriptPackageResource.getHeaderContribution(DetailsPanel.class, DetailsPanel.class.getSimpleName() + ".js"));

        //simple root properties
        WebMarkupContainer simplePropsSection = new WebMarkupContainer("simplePropsSection");

        List<Change> rootSimpleProperties = new ArrayList<Change>();
        for (Change change : changes) {
            if (!change.isCollectionChange()) {
                rootSimpleProperties.add(change);
            }
        }
        Collections.sort(rootSimpleProperties, PROPERTY_COMPARATOR);

        ListView<Change> rootSimpleProps = new ListView<Change>("rootSimpleProps", rootSimpleProperties) {

            @Override
            protected void populateItem(ListItem<Change> item) {
                Change change = item.getModelObject();
                item.add(new Label("propName", change.getPropertyName()));
                item.add(new Label("oldValue", change.getOldValue()));
                item.add(new Label("newValue", change.getNewValue()));
                item.add(new Label("locale", change.getLocale() != null ? change.getLocale().getDisplayName(getLocale()) : ""));
            }
        };

        if (rootSimpleProperties.isEmpty()) {
            simplePropsSection.setVisible(false);
        }
        simplePropsSection.add(rootSimpleProps);
        add(simplePropsSection);

        //collection changes. Collection propery maps to list of changes for that property.
        final Map<String, List<Change>> collectionChangesMap = new HashMap<String, List<Change>>();
        for (Change change : changes) {
            if (change.isCollectionChange()) {
                List<Change> collectionChanges = collectionChangesMap.get(change.getCollectionProperty());
                if (collectionChanges == null) {
                    collectionChanges = new ArrayList<Change>();
                    collectionChangesMap.put(change.getCollectionProperty(), collectionChanges);
                }
                collectionChanges.add(change);
            }
        }

        ListView<String> collectionProps = new ListView<String>("collectionProps", new ArrayList<String>(collectionChangesMap.keySet())) {

            @Override
            protected void populateItem(ListItem<String> item) {
                String collectionPropName = item.getModelObject();
                item.add(new Label("collectionPropName", collectionPropName));


                List<Change> collectionChanges = collectionChangesMap.get(collectionPropName);

                //collection changes-modifications.
                final Map<String, List<Change>> modifiedChangesMap = new HashMap<String, List<Change>>();
                for (Change change : collectionChanges) {
                    if (change.getCollectionModificationStatus() == Change.CollectionModificationStatus.MODIFICATION) {
                        List<Change> modifiedChanges = modifiedChangesMap.get(change.getCollectionObjectId());
                        if (modifiedChanges == null) {
                            modifiedChanges = new ArrayList<Change>();
                            modifiedChangesMap.put(change.getCollectionObjectId(), modifiedChanges);
                        }
                        modifiedChanges.add(change);
                    }
                }

                ListView<String> modifiedItems = new ListView<String>("modifiedItems", new ArrayList<String>(modifiedChangesMap.keySet())) {

                    @Override
                    protected void populateItem(ListItem<String> item) {
                        String collectionObjectId = item.getModelObject();
                        item.add(new Label("item", collectionObjectId));

                        List<Change> itemPropChanges = modifiedChangesMap.get(collectionObjectId);
                        if (itemPropChanges == null) {
                            itemPropChanges = Collections.emptyList();
                        }
                        Collections.sort(itemPropChanges, PROPERTY_COMPARATOR);

                        ListView<Change> itemProps = new ListView<Change>("itemProps", itemPropChanges) {

                            @Override
                            protected void populateItem(ListItem<Change> item) {
                                Change change = item.getModelObject();
                                item.add(new Label("propName", change.getPropertyName()));
                                item.add(new Label("oldValue", change.getOldValue()));
                                item.add(new Label("newValue", change.getNewValue()));
                                item.add(new Label("locale", change.getLocale() != null ? change.getLocale().getDisplayName(getLocale()) : ""));
                            }
                        };
                        item.add(itemProps);
                    }
                };
                item.add(modifiedItems);

                //collection changes-additions.
                List<Change> addedItemChanges = new ArrayList<Change>();
                for (Change change : collectionChanges) {
                    if (change.getCollectionModificationStatus() == Change.CollectionModificationStatus.ADDITION) {
                        addedItemChanges.add(change);
                    }
                }
                WebMarkupContainer addedItemsSection = new WebMarkupContainer("addedItemsSection");
                ListView<Change> addedItems = new ListView<Change>("addedItems", addedItemChanges) {

                    @Override
                    protected void populateItem(ListItem<Change> item) {
                        item.add(new Label("item", item.getModelObject().getCollectionObjectId()));
                    }
                };
                if (addedItemChanges.isEmpty()) {
                    addedItemsSection.setVisible(false);
                }
                item.add(addedItemsSection);
                addedItemsSection.add(addedItems);

                //collections changes-removals.
                List<Change> removedItemChanges = new ArrayList<Change>();
                for (Change change : collectionChanges) {
                    if (change.getCollectionModificationStatus() == Change.CollectionModificationStatus.REMOVAL) {
                        removedItemChanges.add(change);
                    }
                }
                WebMarkupContainer removedItemsSection = new WebMarkupContainer("removedItemsSection");
                ListView<Change> removedItems = new ListView<Change>("removedItems", removedItemChanges) {

                    @Override
                    protected void populateItem(ListItem<Change> item) {
                        item.add(new Label("item", item.getModelObject().getCollectionObjectId()));
                    }
                };
                if (removedItemChanges.isEmpty()) {
                    removedItemsSection.setVisible(false);
                }
                item.add(removedItemsSection);
                removedItemsSection.add(removedItems);

                //collections enables
                List<Change> enabledItemChanges = new ArrayList<Change>();
                for (Change change : collectionChanges) {
                    if (change.getCollectionModificationStatus() == Change.CollectionModificationStatus.ENABLE) {
                        enabledItemChanges.add(change);
                    }
                }
                WebMarkupContainer enabledItemsSection = new WebMarkupContainer("enabledItemsSection");
                ListView<Change> enabledItems = new ListView<Change>("enabledItems", enabledItemChanges) {

                    @Override
                    protected void populateItem(ListItem<Change> item) {
                        item.add(new Label("item", item.getModelObject().getCollectionObjectId()));
                    }
                };
                if (enabledItemChanges.isEmpty()) {
                    enabledItemsSection.setVisible(false);
                }
                item.add(enabledItemsSection);
                enabledItemsSection.add(enabledItems);

                //collections disables
                List<Change> disabledItemChanges = new ArrayList<Change>();
                for (Change change : collectionChanges) {
                    if (change.getCollectionModificationStatus() == Change.CollectionModificationStatus.DISABLE) {
                        disabledItemChanges.add(change);
                    }
                }
                WebMarkupContainer disabledItemsSection = new WebMarkupContainer("disabledItemsSection");
                ListView<Change> disabledItems = new ListView<Change>("disabledItems", disabledItemChanges) {

                    @Override
                    protected void populateItem(ListItem<Change> item) {
                        item.add(new Label("item", item.getModelObject().getCollectionObjectId()));
                    }
                };
                if (disabledItemChanges.isEmpty()) {
                    disabledItemsSection.setVisible(false);
                }
                item.add(disabledItemsSection);
                disabledItemsSection.add(disabledItems);
            }
        };
        add(collectionProps);
    }
}
