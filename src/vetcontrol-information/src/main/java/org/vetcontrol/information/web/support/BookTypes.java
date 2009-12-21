/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.support;

import java.util.Arrays;
import java.util.List;
import org.vetcontrol.information.model.Book1;
import org.vetcontrol.information.model.Book2;

/**
 *
 * @author Artem
 */
public class BookTypes {

    private static final List<Class<?>> BOOK_TYPES = Arrays.asList(new Class<?>[]{
        Book1.class,
        Book2.class,
    });
    
    public static List<Class<?>> getBookTypes() {
       return BOOK_TYPES;
    }
    
}
