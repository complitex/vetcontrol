/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.information.web.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.Department;
import org.vetcontrol.information.web.pages.AddUpdateBookEntryPage;
import org.vetcontrol.information.web.pages.BookPage;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeEdit;
import org.vetcontrol.information.web.pages.custom.cargomode.CargoModeList;
import org.vetcontrol.information.web.pages.custom.department.DepartmentEdit;
import org.vetcontrol.book.BookTypes;
import org.vetcontrol.information.web.pages.custom.department.DepartmentList;

/**
 *
 * @author Artem
 */
public final class BookWebInfoContainer {

    private static class BookWebInfo {

    private Class<? extends WebPage> listPage;
    private Class<? extends WebPage> editPage;
    private PageParameters listPageParameters;

    private BookWebInfo(Class<? extends WebPage> listPage, Class<? extends WebPage> editPage, PageParameters listPageParameters) {
        this.listPage = listPage;
        this.editPage = editPage;
        this.listPageParameters = listPageParameters;
    }
}

    private static Map<Class<?>, BookWebInfo> infoMap;

    static {
        infoMap = new HashMap<Class<?>, BookWebInfo>();
        for (Class commonBookType : BookTypes.common()) {
            PageParameters listPageParams = new PageParameters();
            listPageParams.add(BookPage.BOOK_TYPE, commonBookType.getName());

            BookWebInfo webInfo = new BookWebInfo(BookPage.class, AddUpdateBookEntryPage.class, listPageParams);
            infoMap.put(commonBookType, webInfo);
        }

        //custom book types
        // 1.CargoMode
        infoMap.put(CargoMode.class, new BookWebInfo(CargoModeList.class, CargoModeEdit.class, PageParameters.NULL));

        // 2. Department
        PageParameters listPageParams = new PageParameters();
        listPageParams.add(BookPage.BOOK_TYPE, Department.class.getName());
        BookWebInfo departmentWebInfo = new BookWebInfo(DepartmentList.class, DepartmentEdit.class, listPageParams);
        infoMap.put(Department.class, departmentWebInfo);
    }

    private BookWebInfoContainer() {
    }

    public static Class<? extends WebPage> getListPage(Class<?> bookType){
        return infoMap.get(bookType).listPage;
    }

    public static Class<? extends WebPage> getEditPage(Class<?> bookType){
        return infoMap.get(bookType).editPage;
    }

    public static PageParameters getListPageParameters(Class<?> bookType){
        return infoMap.get(bookType).listPageParameters;
    }
}
