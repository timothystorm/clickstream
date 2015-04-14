package org.storm.clickstream.writer;

import org.storm.clickstream.ClickStream;
import org.storm.clickstream.ClickStreamRequest;
import org.storm.clickstream.ClickStreamWriter;

public class ClickStreamConsoleWriter implements ClickStreamWriter {
    @Override
    public void write(ClickStream session) {
        if (session == null) return;
        System.out.println(session);

        for (ClickStreamRequest request : session) {
            if (request == null) continue;
            System.out.println(request);
        }
    }

    @Override
    public void close() {}
}