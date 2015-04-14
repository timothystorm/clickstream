package org.storm.clickstream;

import static org.junit.Assert.assertNotNull;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.storm.clickstream.CSConstants;
import org.storm.clickstream.CSNoopWriter;
import org.storm.clickstream.ClickStream;
import org.storm.clickstream.ClickStreamFactoryImpl;
import org.storm.clickstream.ClickStreamFilter;
import org.storm.clickstream.ClickStreamListener;

public class ClickStreamLifeCycleTest {
    ClickStreamListener _listener;
    ClickStreamFilter   _filter;
    MockServletContext  _mockServletContext;

    @Before
    public void setUp() throws Exception {
        _listener = new ClickStreamListener();
        _mockServletContext = new MockServletContext();
        _filter = new ClickStreamFilter();
    }

    @After
    public void tearDown() throws Exception {
        if (_listener != null) _listener.contextDestroyed(new ServletContextEvent(_mockServletContext));
        if (_mockServletContext != null) _mockServletContext = null;
    }

    @Test
    public void lifecycle() throws Exception {
        MockServletContext cntx = new MockServletContext();
        cntx.addInitParameter("CLICKSTREAM_FACTORY", ClickStreamFactoryImpl.class.getName());
        cntx.addInitParameter("CLICKSTREAM_WRITER", CSNoopWriter.class.getName());

        _listener.contextInitialized(new ServletContextEvent(cntx));

        HttpSession mockSession = new MockHttpSession(cntx, "123");
        _listener.sessionCreated(new HttpSessionEvent(mockSession));

        MockHttpServletRequest request = new MockHttpServletRequest(_mockServletContext, "GET", "/test");
        request.setSession(mockSession);
        request.addHeader("user-agent", "JUNIT");
        _filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        _listener.sessionDestroyed(new HttpSessionEvent(mockSession));
        _listener.contextDestroyed(new ServletContextEvent(cntx));

        ClickStream clickstream = (ClickStream) mockSession.getAttribute(CSConstants.CS_CLICKSTREAM_KEY);
        assertNotNull(clickstream);
    }
}
