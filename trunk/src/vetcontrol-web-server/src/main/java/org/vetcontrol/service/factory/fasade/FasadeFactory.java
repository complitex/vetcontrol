/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.service.factory.fasade;

import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.service.fasade.app.AuthenticateUserFasade;
import org.vetcontrol.service.fasade.pages.AbstractFasade;

/**
 *
 * @author Artem
 */
public class FasadeFactory {

    private final Logger log = LoggerFactory.getLogger(FasadeFactory.class);
    private static final String SUFFIX = "Fasade";
    private static final String PACKAGE = AbstractFasade.class.getPackage().getName();
    private Map<Class, AbstractFasade> pageFasades = new HashMap<Class, AbstractFasade>();
    private AuthenticateUserFasade applicationFasade = new AuthenticateUserFasade();

    protected FasadeFactory() {
    }
    private static FasadeFactory instance = new FasadeFactory();

    public static FasadeFactory get() {
        return instance;
    }

    public synchronized AbstractFasade getFasade(Class<? extends WebPage> pageClass) throws Exception {
        AbstractFasade fasade = pageFasades.get(pageClass);
        if (fasade == null) {
            String fasadeName = PACKAGE + "." + pageClass.getSimpleName() + SUFFIX;
            try {
                fasade = (AbstractFasade) Thread.currentThread().getContextClassLoader().loadClass(fasadeName).newInstance();
                pageFasades.put(pageClass, fasade);
            } catch (Exception e) {
                log.error("Can not to load class or instantiate instance.");
                throw e;
            }
        }
        return fasade;
    }

    public AuthenticateUserFasade getApplicationFasade() {
        return applicationFasade;
    }
}
