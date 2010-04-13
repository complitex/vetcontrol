package org.vetcontrol.sync;

import org.vetcontrol.entity.Vehicle;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.04.2010 18:06:17
 */
@XmlRootElement
public class SyncVehicle extends SyncRequestEntity{
    private List<Vehicle> vehicles;

    public SyncVehicle() {
    }

    public SyncVehicle(String secureKey, Date updated, List<Vehicle> vehicles) {
        super(secureKey, updated);
        this.vehicles = vehicles;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
