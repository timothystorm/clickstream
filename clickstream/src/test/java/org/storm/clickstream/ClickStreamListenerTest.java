package org.storm.clickstream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.storm.clickstream.CSNoopFactory;
import org.storm.clickstream.ClickStreamListener;
import org.storm.clickstream.writer.ClickStreamConsoleWriter;

public class ClickStreamListenerTest {
    ClickStreamListener _listener;
    MockServletContext  _mockServletContext;

    @Test
    public void contextInitialized() throws Exception {
        ServletContextEvent mockEvent = new ServletContextEvent(new MockServletContext());
        _listener.contextInitialized(mockEvent);
    }

    @Test
    public void contextInitialized_alt_factory() throws Exception {
        MockServletContext cntx = new MockServletContext();
        cntx.addInitParameter("CLICKSTREAM_FACTORY", CSNoopFactory.class.getName());

        // init
        _listener.contextInitialized(new ServletContextEvent(cntx));

        Object factory = ReflectionTestUtils.getField(_listener, "_factory");
        assertNotNull(factory);
        assertTrue(factory instanceof CSNoopFactory);
    }

    @Test
    public void contextInitialized_alt_writer() throws Exception {
        MockServletContext cntx = new MockServletContext();
        cntx.addInitParameter("CLICKSTREAM_WRITER", ClickStreamConsoleWriter.class.getName());

        // init
        _listener.contextInitialized(new ServletContextEvent(cntx));

        Object writer = ReflectionTestUtils.getField(_listener, "_writer");
        assertNotNull(writer);
        assertTrue(writer instanceof ClickStreamConsoleWriter);
    }

    @Test
    public void sessionCreated_sessionDestroyed() throws Exception {
        MockServletContext cntx = new MockServletContext();
        _listener.contextInitialized(new ServletContextEvent(cntx));

        HttpSession mockSession = new MockHttpSession(cntx, "123");
        _listener.sessionCreated(new HttpSessionEvent(mockSession));
        _listener.sessionDestroyed(new HttpSessionEvent(mockSession));
    }

    @Before
    public void setUp() throws Exception {
        _listener = new ClickStreamListener();
        _mockServletContext = new MockServletContext();
    }

    @After
    public void tearDown() throws Exception {
        if (_listener != null) _listener.contextDestroyed(new ServletContextEvent(_mockServletContext));
        if (_mockServletContext != null) _mockServletContext = null;
    }
}
