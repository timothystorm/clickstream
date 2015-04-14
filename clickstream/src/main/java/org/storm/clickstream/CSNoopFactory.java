package org.storm.clickstream;

import javax.servlet.http.HttpSession;

class CSNoopFactory implements ClickStreamFactory {

    @Override
    public ClickStream create(HttpSession session) {
        return null;
    }
}
