package org.storm.clickstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.storm.clickstream.CSConstants;
import org.storm.clickstream.ClickStream;
import org.storm.clickstream.ClickStreamFilter;

public class ClickStreamFilterTest {
    ClickStreamFilter  _filter;
    MockServletContext _mockContext;

    @Before
    public void setUp() throws Exception {
        _mockContext = new MockServletContext();
        _filter = new ClickStreamFilter();
    }

    @After
    public void tearDown() throws Exception {
        _filter = null;
        _mockContext = null;
    }

    @Test
    public void init() throws Exception {
        _filter.init(new MockFilterConfig(_mockContext));
    }

    @Test
    public void init_params() throws Exception {
        MockFilterConfig mockConfig = new MockFilterConfig(_mockContext);
        mockConfig.addInitParameter("INCLUDE_BOTS", "yes");
        _filter.init(mockConfig);

        assertTrue(_filter.includeBots());
    }

    @Test
    public void doFilter() throws Exception {
        // init filter
        _filter.init(new MockFilterConfig(_mockContext));

        // init input
        MockHttpSession mockSession = new MockHttpSession(_mockContext, "123");
        mockSession.setAttribute(CSConstants.CS_CLICKSTREAM_KEY, new ClickStream(mockSession));

        MockHttpServletRequest mockRequest = new MockHttpServletRequest(_mockContext);
        mockRequest.setSession(mockSession);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        MockFilterChain mockChain = new MockFilterChain();

        // execute
        _filter.doFilter(mockRequest, mockResponse, mockChain);

        // verify
        Object filterValue = mockRequest.getAttribute(CSConstants.CS_FILTER_KEY);
        assertNotNull(filterValue);
        assertEquals(Boolean.TRUE, filterValue);

        HttpSession session = mockRequest.getSession();
        assertNotNull(session);

        Object sessionValue = session.getAttribute(CSConstants.CS_CLICKSTREAM_KEY);
        assertNotNull(sessionValue);
        assertTrue(sessionValue instanceof ClickStream);

        assertSame(mockRequest, mockChain.getRequest());
        assertSame(mockResponse, mockChain.getResponse());
    }
}
