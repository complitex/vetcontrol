package org.vetcontrol.entity;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlType;

@Embeddable
@XmlType
public class StringCultureId implements java.io.Serializable {
    private Long id;
    private String locale;

    public StringCultureId() {}

    public StringCultureId(String locale) {
        this.locale = locale;
    }

    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "locale", nullable = false, length = 2)
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if ((other == null)) {
            return false;
        }
        if (!(other instanceof StringCultureId)) {
            return false;
        }
        StringCultureId castOther = (StringCultureId) other;

        return (this.getId().equals(castOther.getId()))
                && ((this.getLocale().equals(castOther.getLocale())) || (this.getLocale() != null && castOther.getLocale() != null && this.getLocale().equals(castOther.getLocale())));
    }

    @Override
    public int hashCode() {
        long result = 17;

        result = 37 * result + this.getId();
        result = 37 * result + (getLocale() == null ? 0 : this.getLocale().hashCode());
        return (int)result;
    }
}


