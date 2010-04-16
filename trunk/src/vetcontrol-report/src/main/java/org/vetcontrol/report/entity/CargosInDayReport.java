/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.entity;

import org.vetcontrol.report.commons.entity.Ordered;
import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class CargosInDayReport extends Ordered implements Serializable {

    private String cargoTypeName;
    private String cargoSenderName;
    private String cargoSenderCountry;
    private String cargoReceiverName;
    private String cargoReceiverAddress;
    private String cargoProducerName;
    private String vehicleType;
    private Number count;
    private String unitTypeName;

    public String getCargoProducerName() {
        return cargoProducerName;
    }

    public void setCargoProducerName(String cargoProducerName) {
        this.cargoProducerName = cargoProducerName;
    }

    public String getCargoReceiverName() {
        return cargoReceiverName;
    }

    public void setCargoReceiverName(String cargoReceiverName) {
        this.cargoReceiverName = cargoReceiverName;
    }

    public String getCargoSenderName() {
        return cargoSenderName;
    }

    public void setCargoSenderName(String cargoSenderName) {
        this.cargoSenderName = cargoSenderName;
    }

    public String getCargoTypeName() {
        return cargoTypeName;
    }

    public void setCargoTypeName(String cargoTypeName) {
        this.cargoTypeName = cargoTypeName;
    }

    public Number getCount() {
        return count;
    }

    public void setCount(Number count) {
        this.count = count;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCargoReceiverAddress() {
        return cargoReceiverAddress;
    }

    public void setCargoReceiverAddress(String cargoReceiverAddress) {
        this.cargoReceiverAddress = cargoReceiverAddress;
    }

    public String getCargoSenderCountry() {
        return cargoSenderCountry;
    }

    public void setCargoSenderCountry(String cargoSenderCountry) {
        this.cargoSenderCountry = cargoSenderCountry;
    }

}
