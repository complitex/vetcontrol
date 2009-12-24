/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Artem
 */
@Entity
@Table(name = "locales", catalog = "project1")
public class Locale implements Serializable {

    private String locale;
    private boolean isSystem;

    public Locale() {
    }

    @Column(name = "isSystem")
    public boolean isSystem() {
        return isSystem;
    }

    @Id
    @Column(name = "locale", length = 2)
    public String getLocale() {
        return locale;
    }

    private void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    private void setLocale(String locale) {
        this.locale = locale;
    }
}
