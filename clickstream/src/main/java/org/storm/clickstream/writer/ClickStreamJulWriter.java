package org.storm.clickstream.writer;

import java.util.logging.Logger;

import org.storm.clickstream.ClickStream;
import org.storm.clickstream.ClickStreamRequest;
import org.storm.clickstream.ClickStreamWriter;

public class ClickStreamJulWriter implements ClickStreamWriter {
    private final Logger      _logger = Logger.getLogger(CSWriterConstants.CS_LOGGER_NAME);
    private static final char PIPE    = '|';

    @Override
    public void write(ClickStream cs) {
        StringBuilder str = new StringBuilder();

        append(cs, str);
        for (ClickStreamRequest request : cs) {
            append(request, str);
        }

        _logger.info(str.toString());
    }

    protected void append(ClickStreamRequest csr, StringBuilder str) {
        str.append(csr.getMethod()).append(PIPE);
        str.append(csr.getUrl()).append(PIPE);
        str.append('\n');
    }

    protected void append(ClickStream cs, StringBuilder str) {
        str.append(cs.getId()).append(PIPE);
        str.append(cs.getStart()).append(PIPE);
        str.append(cs.getEnd());
        str.append('\n');
    }

    @Override
    public void close() {}
}