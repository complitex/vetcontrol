/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

/**
 *
 * @author Artem
 */
public class Id {

    private final Long start;

    private final Long end;

    private Long currentId;

    public Id(Long start, Long end) {
        this.start = start;
        this.end = end;
        this.currentId = start;
    }

    public Long getAndIncrement() {
        return currentId++;
    }

    public boolean canIncrement() {
        return currentId < end;
    }
}
