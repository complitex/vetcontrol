/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.entity;

import org.vetcontrol.report.commons.entity.Ordered;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Artem
 */
public class RegionalControlReport extends Ordered implements Serializable {

    private Date cargoArrived;
    private String cargoProducerName;
    private String cargoReceiverName;
    private String cargoReceiverAddress;
    private String cargoTypeName;
    private String cargoTypeCode;
    private Number count;
    private String unitTypeName;
    private String movementType;

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public Date getCargoArrived() {
        return cargoArrived;
    }

    public void setCargoArrived(Date cargoArrived) {
        this.cargoArrived = cargoArrived;
    }

    public String getCargoTypeCode() {
        return cargoTypeCode;
    }

    public void setCargoTypeCode(String cargoTypeCode) {
        this.cargoTypeCode = cargoTypeCode;
    }

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

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementTypeName) {
        this.movementType = movementTypeName;
    }

    public String getCargoReceiverAddress() {
        return cargoReceiverAddress;
    }

    public void setCargoReceiverAddress(String cargoReceiverAddress) {
        this.cargoReceiverAddress = cargoReceiverAddress;
    }
}
