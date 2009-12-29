/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.vetcontrol.information.model.CountryBook;
import org.vetcontrol.information.model.Registeredproducts;
import org.vetcontrol.information.model.VehicleType;

/**
 *
 * @author Artem
 */
public class BookTypes {

    public static final List<Class> BOOK_TYPES = Collections.unmodifiableList(Arrays.asList(new Class[]{
        CountryBook.class,
        Registeredproducts.class,
        VehicleType.class,
    }));

    @Deprecated
    public static List<Class<?>> getBookTypes() {
       return null;
    }
    
}
