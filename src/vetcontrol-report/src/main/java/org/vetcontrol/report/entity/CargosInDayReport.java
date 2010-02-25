/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.report.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class CargosInDayReport implements Serializable {

    private String cargoTypeName;
    private String cargoSenderName;
    private String cargoReceiverName;
    private String cargoProducerName;
    private Number car;
    private Number ship;
    private Number container;
    private Number carriage;
    private Number aircraft;
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

    public Number getAircraft() {
        return aircraft;
    }

    public void setAircraft(Number aircraft) {
        this.aircraft = aircraft;
    }

    public Number getCar() {
        return car;
    }

    public void setCar(Number car) {
        this.car = car;
    }

    public Number getCarriage() {
        return carriage;
    }

    public void setCarriage(Number carriage) {
        this.carriage = carriage;
    }

    public Number getContainer() {
        return container;
    }

    public void setContainer(Number container) {
        this.container = container;
    }

    public Number getShip() {
        return ship;
    }

    public void setShip(Number ship) {
        this.ship = ship;
    }
}
