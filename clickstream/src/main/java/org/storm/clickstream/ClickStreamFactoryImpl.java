package org.storm.clickstream;

import javax.servlet.http.HttpSession;

public class ClickStreamFactoryImpl implements ClickStreamFactory {
    @Override
    public ClickStream create(HttpSession session) {
        return new ClickStream(session);
    }
}
