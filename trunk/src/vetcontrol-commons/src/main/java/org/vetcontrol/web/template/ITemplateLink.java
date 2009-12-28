package org.vetcontrol.web.template;

import org.apache.wicket.Page;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 23.12.2009 17:09:34
 *
 * Интерфейс для локализованной ссылки на страницу модуля
 */
public interface ITemplateLink extends Serializable{

    /**
     * Название ссылки в зависимости от текущей локали
     * @param locale Текущая локаль
     * @return Название ссылки
     */
    public String getLabel(Locale locale);

    /**
     * Страница на которую указывает ссылка
     * @return Страница модуля
     */
    public Class<? extends Page> getPage();
}
