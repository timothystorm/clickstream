package org.storm.clickstream;

import javax.servlet.http.HttpSession;

public interface ClickStreamFactory {
    ClickStream create(HttpSession session);
}
