/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.util.web;

import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.PageParameters;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.Department;
import org.vetcontrol.information.web.pages.AddUpdateBookEntryPage;
import org.vetcontrol.information.web.pages.BookPage;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeEdit;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeList;
import org.vetcontrol.information.web.pages.custom.department.DepartmentEdit;
import org.vetcontrol.util.book.BookTypes;

/**
 *
 * @author Artem
 */
public final class BookTypeWebInfoUtil {

    private static Map<Class<?>, BookWebInfo> info;

    static {
        info = new HashMap<Class<?>, BookWebInfo>();
        for (Class commonBookType : BookTypes.common()) {
            PageParameters listPageParams = new PageParameters();
            listPageParams.add(BookPage.BOOK_TYPE, commonBookType.getName());

            BookWebInfo webInfo = new BookWebInfo(BookPage.class, AddUpdateBookEntryPage.class, listPageParams);
            info.put(commonBookType, webInfo);
        }

        //custom book types
        // 1.CargoMode
        info.put(CargoMode.class, new BookWebInfo(CargoModeList.class, CargoModeEdit.class, PageParameters.NULL));

        // 2. Department
        PageParameters listPageParams = new PageParameters();
        listPageParams.add(BookPage.BOOK_TYPE, Department.class.getName());
        BookWebInfo departmentWebInfo = new BookWebInfo(BookPage.class, DepartmentEdit.class, listPageParams);
        info.put(Department.class, departmentWebInfo);
    }

    private BookTypeWebInfoUtil() {
    }

    public static BookWebInfo getInfo(Class<?> bookType) {
        return info.get(bookType);
    }
}
