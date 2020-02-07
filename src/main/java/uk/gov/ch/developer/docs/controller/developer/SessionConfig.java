package uk.gov.ch.developer.docs.controller.developer;

import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionFactory;

public class SessionConfig {

    private static EnvironmentReader reader = new EnvironmentReaderImpl();

    public static final String COOKIE_NAME = reader.getMandatoryString("COOKIE_NAME");
    public static final String CHS_SESSION_REQUEST_ATT_KEY = "chsSession";

    public Session getSession(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        // Get the CHS session
        String cookieId = getCHSSessionCookieId(request);
        Session chSession = getSessionByCookieId(cookieId);

        return chSession;
    }

    private String getCHSSessionCookieId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;

        Cookie chsCookie = Arrays.stream(cookies)
                .filter((Cookie cookie) -> cookie.getName().equals(COOKIE_NAME)).findFirst()
                .orElse(null);

        if (chsCookie == null) {
            return null;
        }

        return chsCookie.getValue();
    }

    private Session getSessionByCookieId(String cookieId) {
        return SessionFactory.getSessionByCookieId(SessionFactory.getDefaultStoreImpl(), cookieId);
    }
}