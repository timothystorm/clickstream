package org.storm.clickstream;

import static org.storm.clickstream.CSConstants.CS_CLICKSTREAM_FACTORY;
import static org.storm.clickstream.CSConstants.CS_CLICKSTREAM_KEY;
import static org.storm.clickstream.CSConstants.CS_WRITER;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * 
 * @author Timothy Storm
 */
public class ClickStreamListener implements ServletContextListener, HttpSessionListener {
    private static final ClickStreamFactory FALLBACK_FACTORY = new CSNoopFactory();
    private static final ClickStreamWriter  FALLBACK_WRITER  = new CSNoopWriter();
    private ServletContext                  _cntx;
    private ClickStreamWriter               _writer;
    private ClickStreamFactory              _factory;

    public ClickStreamListener() {}

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (_writer != null) _writer.close();
        } catch (Exception defensive) {} finally {
            _factory = null;
            _writer = null;
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        _cntx = event.getServletContext();

        // init clickstream factory
        _factory = newInstance(_cntx.getInitParameter(CS_CLICKSTREAM_FACTORY.toUpperCase()));
        if (_factory == null) _factory = newInstance(_cntx.getInitParameter(CS_CLICKSTREAM_FACTORY.toLowerCase()));
        if (_factory == null) _factory = FALLBACK_FACTORY;

        // init writer
        _writer = newInstance(_cntx.getInitParameter(CS_WRITER.toUpperCase()));
        if (_writer == null) _writer = newInstance(_cntx.getInitParameter(CS_WRITER.toLowerCase()));
        if (_writer == null) _writer = FALLBACK_WRITER;
    }

    @SuppressWarnings("unchecked")
    private <T> T newInstance(String name) {
        try {
            if (name == null) return null;
            return (T) Class.forName(name).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException("failed to instance '" + name
                    + "' does it exist and have a no-arg constructor)", e);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        try {
            final HttpSession session = event.getSession();
            if (session == null /* pathological case */) return;

            // only store the clickstream once per session
            if (session.getAttribute(CS_CLICKSTREAM_KEY) == null) {
                ClickStream clickstream = _factory == null ? null : _factory.create(session);
                if (clickstream != null) session.setAttribute(CS_CLICKSTREAM_KEY, clickstream);
            }
        } catch (Exception defensive) {}
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            final HttpSession session = event.getSession();
            if (session == null /* expired */) return;

            ClickStream csSession = (ClickStream) session.getAttribute(CS_CLICKSTREAM_KEY);
            if (csSession == null /* never stored */) return;
            else csSession.setEnd(new DateTime(DateTimeZone.UTC));

            if (_writer != null) _writer.write(csSession);
        } catch (Exception defensive) {}
    }
}
