package org.vetcontrol.web.security;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
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

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 * Date: 16.12.2009 20:18:49
 *
 * Wicket приложение, которое использует авторизацию сервлет контейнера.
 */
public abstract class ServeltAuthWebApplication extends WebApplication
        implements IRoleCheckingStrategy, IUnauthorizedComponentInstantiationListener {

    @Override
    protected void init() {
        super.init();
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
		getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
    }

    @Override
    public boolean hasAnyRole(Roles roles) {
        HttpServletRequest request = ((WebRequestCycle) RequestCycle.get()).getWebRequest().getHttpServletRequest();
        if (roles != null){
            for (String role : roles){
                if (request.isUserInRole(role)) return true;
            }
        }

        return false;
    }

    @Override
    public void onUnauthorizedInstantiation(Component component) {
        if (component instanceof Page){
            WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
			if (webRequestCycle.getWebRequest().getHttpServletRequest().getUserPrincipal() == null){
                
				throw new RedirectToUrlException("/login.jsp");
			}
			else{
				throw new UnauthorizedInstantiationException(component.getClass());
			}
		} else{
			throw new UnauthorizedInstantiationException(component.getClass());
		}
    }
}
