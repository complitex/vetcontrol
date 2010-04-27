/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.document.service;

import java.util.HashMap;
import java.util.Map;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.MovementType;

/**
 *
 * @author Artem
 */
public final class AvailableMovementTypes {

    private static Map<Class<?>, MovementType[]> SUPPORTED_MOVEMENT_TYPES_MAP;

    static {
        SUPPORTED_MOVEMENT_TYPES_MAP = new HashMap<Class<?>, MovementType[]>();
        SUPPORTED_MOVEMENT_TYPES_MAP.put(DocumentCargo.class, new MovementType[]{MovementType.IMPORT, MovementType.TRANSIT});
    }

    private AvailableMovementTypes() {
    }

    public static MovementType[] get(Class<?> documentClazz) {
        return SUPPORTED_MOVEMENT_TYPES_MAP.get(documentClazz);
    }
}
