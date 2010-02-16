package org.vetcontrol.entity;

import javax.xml.bind.annotation.XmlEnum;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 21.12.2009 16:00:23
 *
 * Список групп пользователей
 */
@XmlEnum
public enum SecurityGroup {
    ADMINISTRATORS,
    DEPARTMENT_OFFICERS,
    LOCAL_OFFICERS, LOCAL_OFFICERS_EDIT,    
    LOCAL_OFFICERS_DEP_VIEW, LOCAL_OFFICERS_DEP_EDIT,
    LOCAL_OFFICERS_DEP_CHILD_VIEW, LOCAL_OFFICERS_DEP_CHILD_EDIT,
    MOBILE_OFFICERS
}
