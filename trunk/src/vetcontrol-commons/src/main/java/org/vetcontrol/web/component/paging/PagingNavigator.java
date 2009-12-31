package org.vetcontrol.web.component.paging;

import org.apache.wicket.markup.html.navigation.paging.IPageable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.12.2009 7:29:27
 */
public class PagingNavigator extends org.apache.wicket.markup.html.navigation.paging.PagingNavigator {
    public PagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }
}
