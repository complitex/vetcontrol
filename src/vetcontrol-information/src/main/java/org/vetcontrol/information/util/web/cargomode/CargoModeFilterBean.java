/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.information.util.web.cargomode;

import java.io.Serializable;
import org.vetcontrol.entity.CargoMode;

/**
 *
 * @author Artem
 */
public class CargoModeFilterBean implements Serializable {

    public CargoModeFilterBean() {
    }

    private String uktzed;

    public String getUktzed() {
        return uktzed;
    }

    public void setUktzed(String uktzed) {
        this.uktzed = uktzed;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private CargoMode parent;

    public CargoMode getParent() {
        return parent;
    }

    public void setParent(CargoMode cargoMode) {
        this.parent = cargoMode;
    }
}
