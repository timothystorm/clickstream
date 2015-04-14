package org.storm.clickstream;

class CSNoopWriter implements ClickStreamWriter {
    @Override
    public void write(ClickStream session) {}

    @Override
    public void close() {}
}
