/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.entity;

/**
 *
 * @author Artem
 */
public enum VehicleType {

    CAR(true), SHIP(false), CONTAINER(true), CARRIAGE(true), AIRCRAFT(false);
    private boolean isCompound;

    public boolean isCompound() {
        return isCompound;
    }

    private VehicleType(boolean isCompound) {
        this.isCompound = isCompound;
    }
}
