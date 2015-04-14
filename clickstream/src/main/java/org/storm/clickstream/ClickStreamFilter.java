package org.storm.clickstream;

import static org.storm.clickstream.CSConstants.CS_CLICKSTREAM_KEY;
import static org.storm.clickstream.CSConstants.CS_FILTER_KEY;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;

public class ClickStreamFilter implements Filter {
    private FilterConfig _config;
    private boolean      _includeBots;

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            if (!(request instanceof HttpServletRequest)) return;

            System.out.println("handling request");
            final HttpServletRequest req = (HttpServletRequest) request;
            if (process(req)) {
                if (req.getAttribute(CS_FILTER_KEY) == null) {
                    req.setAttribute(CS_FILTER_KEY, Boolean.TRUE);
                    HttpSession session = req.getSession();
                    ClickStream cs = (ClickStream) session.getAttribute(CS_CLICKSTREAM_KEY);
                    if (cs != null /* not added by listener */) cs.addRequest(new ClickStreamRequest(req));
                }
            }
        } finally {
            chain.doFilter(request, response);
        }
    }

    public boolean includeBots() {
        return _includeBots;
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        _config = config;

        // init bot param
        String botParam = _config.getInitParameter(CSConstants.CS_INCLUDE_BOTS);
        _includeBots = botParam == null ? true : BooleanUtils.toBoolean(botParam);
    }

    protected boolean process(HttpServletRequest request) {
        if (request == null) return false;
        if (!includeBots() && CSBotChecker.isBot(request)) return false;
        return true;
    }
}
