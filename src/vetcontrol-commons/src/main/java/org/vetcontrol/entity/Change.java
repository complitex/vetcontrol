package org.vetcontrol.entity;

import java.io.Serializable;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.annotations.Parent;

@Embeddable
public class Change implements Serializable {

    public static enum CollectionModificationStatus {

        MODIFICATION, ADDITION, REMOVAL, ENABLE, DISABLE;
    }

    @Parent
    private Log log;

    /**
     * For collection properties only
     */
    @Column(name = "collection_object_id", nullable = true, length = 500)
    private String collectionObjectId;

    @Column(name = "property_name", nullable = true, length = 50)
    private String propertyName;

    /**
     * For collection properties only
     */
    @Column(name = "collection_property", nullable = true, length = 50)
    private String collectionProperty;

    @Column(name = "old_value", nullable = true, length = 500)
    private String oldValue;

    @Column(name = "new_value", nullable = true, length = 500)
    private String newValue;

    @Column(name = "locale", nullable = true, length = 10)
    private Locale locale;

    /**
     * For collection properties only
     */
    @Column(name = "collection_modification_status", nullable = true, length = 15)
    @Enumerated(EnumType.STRING)
    private CollectionModificationStatus collectionModificationStatus;

    public Change() {
    }

    public String getNewValue() {
        return newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public CollectionModificationStatus getCollectionModificationStatus() {
        return collectionModificationStatus;
    }

    public void setCollectionModificationStatus(CollectionModificationStatus collectionModificationStatus) {
        this.collectionModificationStatus = collectionModificationStatus;
    }

    public String getCollectionObjectId() {
        return collectionObjectId;
    }

    public void setCollectionObjectId(String collectionObjectId) {
        this.collectionObjectId = collectionObjectId;
    }

    public String getCollectionProperty() {
        return collectionProperty;
    }

    public void setCollectionProperty(String collectionProperty) {
        this.collectionProperty = collectionProperty;
    }

    public Log getLog() {
        return log;
    }

    private void setLog(Log log) {
        this.log = log;
    }

    public boolean isCollectionChange() {
        return getCollectionModificationStatus() != null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Change: {\n\t").
                append("property name : ").append(getPropertyName()).append(",\n\t").
                append("old value : ").append(getOldValue()).append(",\n\t").
                append("new value : ").append(getNewValue()).append(",\n\t").
                append("locale : ").append(getLocale() != null ? getLocale().getLanguage() : null).append(",\n\t").
                append("status : ").append(getCollectionModificationStatus()).append(",\n\t").
                append("collection object id : ").append(getCollectionObjectId()).append(",\n\t").
                append("collection property : ").append(getCollectionProperty()).
                append("\n}");
        return stringBuilder.toString();
    }
}
