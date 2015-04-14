package org.storm.clickstream;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.storm.clickstream.ClickStream;
import org.storm.clickstream.ClickStreamRequest;

public class ClickStreamTest {
    public static void main(String[] args) {
        ClickStream cs = new ClickStream(new MockHttpSession());
        for (int i = 0; i < 5_000_000; i++) {
            if(i % 100_000 == 0) System.out.println(i);
            cs.addRequest(new ClickStreamRequest(new MockHttpServletRequest("GET", "/test")));
        }
        System.out.println(cs);
    }
}
