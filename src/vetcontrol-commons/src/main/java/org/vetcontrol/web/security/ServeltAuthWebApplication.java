package org.vetcontrol.web.security;

import org.apache.wicket.Component;
import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycle;

import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.persistence.CookieValuePersister;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 20:18:49
 *
 * Wicket приложение, которое использует авторизацию сервлет контейнера.
 */
public abstract class ServeltAuthWebApplication extends WebApplication
        implements IRoleCheckingStrategy, IUnauthorizedComponentInstantiationListener {

    private static final String MANUAL_LOGOUT_COOKIE = "MANUAL_LOGOUT";
    private static final Logger log = LoggerFactory.getLogger(ServeltAuthWebApplication.class);

    @Override
    protected void init() {
        super.init();
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
    }

    @Override
    public boolean hasAnyRole(Roles roles) {
        HttpServletRequest request = ((WebRequestCycle) RequestCycle.get()).getWebRequest().getHttpServletRequest();
        if (roles != null) {
            for (String role : roles) {
                if (request.isUserInRole(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyRole(String... roles){
        return hasAnyRole(new Roles(roles));
    }

    @Override
    public void onUnauthorizedInstantiation(Component component) {
        WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
        HttpServletRequest servletRequest = webRequestCycle.getWebRequest().getHttpServletRequest();
        String sessionIDFromClient = servletRequest.getRequestedSessionId();
        boolean sessionIsExist = servletRequest.getSession(false) != null;

        if (log.isDebugEnabled()) {
            log.debug("Session id from client : " + sessionIDFromClient + ", current session is exist : " + sessionIsExist);
        }

        if (!Strings.isEmpty(sessionIDFromClient) && !sessionIsExist) {
            boolean isManualLogout = false;

            CookieValuePersister cookieValuePersister = new CookieValuePersister();
            String cookie = cookieValuePersister.load(MANUAL_LOGOUT_COOKIE);
            if (cookie != null) {

                if (log.isDebugEnabled()) {
                    log.debug("That request is redirect to home page after manual logout.");
                }

                isManualLogout = true;
                cookieValuePersister.clear(MANUAL_LOGOUT_COOKIE);
            }

            if (!isManualLogout) {

                if (log.isDebugEnabled()) {
                    log.debug("That request is attempt to access application page after session has been expired.");
                }

                throw new RestartResponseException(getApplicationSettings().getPageExpiredErrorPage());
            }
        }

        if (servletRequest.getUserPrincipal() == null) {
            throw new RedirectToUrlException("/login.jsp");
        } else {
            throw new UnauthorizedInstantiationException(component.getClass());
        }

    }

    /**
     * Helper method in order for logout. Must be used in pages where logout action is required.
     */
    public void logout() {
        CookieValuePersister cookieValuePersister = new CookieValuePersister();
        cookieValuePersister.save(MANUAL_LOGOUT_COOKIE, MANUAL_LOGOUT_COOKIE);
        Session.get().invalidateNow();
        RequestCycle.get().setResponsePage(getHomePage());
    }
}
