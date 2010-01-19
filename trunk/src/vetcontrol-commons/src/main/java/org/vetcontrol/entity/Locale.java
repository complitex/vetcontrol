package org.vetcontrol.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "locales")
public class Locale implements Serializable {

    private String language;
    private boolean isSystem;

    public Locale() {
    }

    @Column(name = "isSystem")
    public boolean isSystem() {
        return isSystem;
    }

    @Id
    @Column(name = "language", length = 2)
    public String getLanguage() {
        return language;
    }

    private void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    private void setLanguage(String locale) {
        this.language = locale;
    }
}
