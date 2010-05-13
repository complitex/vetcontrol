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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringCultureId)) return false;

        StringCultureId that = (StringCultureId) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }
}


