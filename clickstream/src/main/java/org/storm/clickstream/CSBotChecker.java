package org.storm.clickstream;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * Determines if a request is actually a bot or spider. Bot list is read from the optional clickstream configuration
 * :clickstream.xml.
 * 
 * @author <a href="mailto:timothy.storm@fedex.com">Timothy Storm</a>
 */
class CSBotChecker {
    public static boolean isBot(HttpServletRequest request) {
        Set<String> agents = CSConfigLoader.getInstance().getConfig().getBotAgents();
        Set<String> hosts = CSConfigLoader.getInstance().getConfig().getBotHosts();

        if (request.getRequestURI().indexOf("robots.txt") != -1) {
            // there is a specific request for the robots.txt file, so we assume
            // it must be a robot (only robots request robots.txt)
            return true;
        }

        String userAgent = request.getHeader("user-agent");
        if (agents.contains(userAgent)) return true;

        String remoteHost = request.getRemoteHost(); // requires a DNS lookup
        if (remoteHost != null && remoteHost.length() > 0 && remoteHost.charAt(remoteHost.length() - 1) > 64) {
            if (hosts.contains(remoteHost)) return true;
        }

        return false;
    }
}
