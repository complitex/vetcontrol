/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.CountryBook;
import org.vetcontrol.entity.Registeredproducts;
import org.vetcontrol.entity.VehicleType;

/**
 *
 * @author Artem
 */
public class BookTypes {

    public static final List<Class> BOOK_TYPES = Collections.unmodifiableList(Arrays.asList(new Class[]{
        Department.class,
        CountryBook.class,
        Registeredproducts.class,
        VehicleType.class,
    }));

    @Deprecated
    public static List<Class<?>> getBookTypes() {
       return null;
    }
    
}
