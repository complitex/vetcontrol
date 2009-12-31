package org.vetcontrol.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.12.2009 4:21:43
 *
 * Класс слушает создание http сессий и запросов, и сохраняет в сессию информацию
 * об авторизованном пользователе. 
 */
@WebListener
public class SecurityWebListener implements HttpSessionListener, ServletRequestListener {
    private static final Logger log = LoggerFactory.getLogger(SecurityWebListener.class);

    public final static String PRINCIPAL = "org.vetcontrol.web.security.PRINCIPAL";

    private final static ConcurrentHashMap<String, HttpSession> activeSession = new ConcurrentHashMap<String, HttpSession>();

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        activeSession.put(httpSessionEvent.getSession().getId(), httpSessionEvent.getSession());
        log.debug("session created: " + httpSessionEvent.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        activeSession.remove(httpSessionEvent.getSession().getId());
        log.debug("session destroyed: " + httpSessionEvent.getSession().getId());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestEvent.getServletRequest();
        if (httpServletRequest.getUserPrincipal()!=null){
            httpServletRequest.getSession().setAttribute(PRINCIPAL, httpServletRequest.getUserPrincipal().getName());
        }
    }

    public static synchronized List<HttpSession> getSessions(String principal){
        List<HttpSession> sessions = new ArrayList<HttpSession>();

        for (HttpSession session : activeSession.values()){
            if(principal.equals(session.getAttribute(PRINCIPAL))){
                sessions.add(session);
            }
        }

        return sessions;
    }
}
