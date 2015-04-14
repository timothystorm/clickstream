package org.storm.clickstream;

public interface ClickStreamWriter {
    void write(ClickStream session);

    void close();
}
