package org.vetcontrol.entity;

import org.vetcontrol.util.book.entity.annotation.ValidProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stringculture")
public class StringCulture implements java.io.Serializable {

    private StringCultureId id;
    private String value;

    public StringCulture() {}

    public StringCulture(StringCultureId id) {
        this.id = id;
    }

    public StringCulture(StringCultureId id, String value) {
        this.id = id;
        this.value = value;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column =
        @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "locale", column =
        @Column(name = "locale", nullable = false, length = 2))})
    public StringCultureId getId() {
        return this.id;
    }

    public void setId(StringCultureId id) {
        this.id = id;
    }

    @Column(name = "value", length = 1024)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    private Date version;

    @Version
    @ValidProperty(false)
    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }
}


