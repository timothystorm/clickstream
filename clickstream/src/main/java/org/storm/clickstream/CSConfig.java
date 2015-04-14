package org.storm.clickstream;

import java.util.HashSet;
import java.util.Set;

class CSConfig {
    private Set<String> _botAgents = new HashSet<String>();
    private Set<String> _botHosts  = new HashSet<String>();

    public void addBotAgent(String agent) {
        _botAgents.add(agent);
    }

    public void addBotHost(String host) {
        _botHosts.add(host);
    }

    public Set<String> getBotAgents() {
        return _botAgents;
    }

    public Set<String> getBotHosts() {
        return _botHosts;
    }
}
