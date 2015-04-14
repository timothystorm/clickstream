package org.storm.clickstream;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ClickStreamRequest implements Comparable<ClickStreamRequest>, Serializable {
    private static final long         serialVersionUID = ClickStreamVersion.SVUID;

    private final String              _characterEncoding, _contentType, _address, _host, _method, _scheme;
    private final int                 _contentLength, _port;
    private final Cookie[]            _cookies;
    private final Map<String, String> _headers         = new HashMap<>();
    private final List<Locale>        _locales;
    private final String              _serverName, _uri, _query, _remoteUser;

    public ClickStreamRequest(HttpServletRequest request) {
        _contentLength = request.getContentLength();
        _characterEncoding = request.getCharacterEncoding();
        _contentType = request.getContentType();
        _locales = request.getLocales() == null ? null : Collections.list(request.getLocales());
        _address = request.getRemoteAddr();
        _host = request.getRemoteHost();
        _method = request.getMethod();
        _scheme = request.getScheme();
        _serverName = request.getServerName();
        _port = request.getServerPort();
        _uri = request.getRequestURI();
        _query = request.getQueryString();
        _cookies = request.getCookies();
        _remoteUser = request.getRemoteUser();

        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
            String key = names.nextElement();

            if (key == null) continue;
            if (_headers.containsKey(key)) continue;
            _headers.put(key, request.getHeader(key));
        }
    }

    @Override
    public String toString() {
        ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        str.append("method", getMethod());
        str.append("url", getUrl());
        str.append("user-agent", getUserAgent());
        return str.toString();
    }

    @Override
    public int compareTo(ClickStreamRequest other) {
        if (other == null) return 1;
        if (other == this) return 0;

        // compare URL attributes only
        CompareToBuilder compare = new CompareToBuilder();
        compare.append(getMethod(), other.getMethod());
        compare.append(getScheme(), other.getScheme());
        compare.append(getServerName(), other.getServerName());
        compare.append(getPort(), other.getPort());
        compare.append(getUri(), other.getUri());
        compare.append(getQuery(), other.getQuery());
        return compare.toComparison();
    }

    public String getAccept() {
        return getHeader("accept");
    }

    public String getAcceptCharset() {
        return getHeader("accept-charset");
    }

    public String getAcceptEncoding() {
        return getHeader("accept-encoding");
    }

    public String getAddress() {
        return _address;
    }

    public String getAuthorization() {
        return getHeader("authorization");
    }

    public String getCacheControl() {
        return getHeader("cache-control");
    }

    public String getCharacterEncoding() {
        return _characterEncoding;
    }

    public String getConnection() {
        return getHeader("connection");
    }

    public int getContentLength() {
        return _contentLength;
    }

    public String getContentType() {
        return _contentType;
    }

    public List<Cookie> getCookies() {
        return _cookies == null ? Collections.<Cookie> emptyList() : Arrays.asList(_cookies);
    }

    public String getHeader(String name) {
        if (name == null) return null;
        return _headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(_headers);
    }

    public String getHost() {
        return _host;
    }

    public Locale getLocale() {
        List<Locale> locales = getLocales();
        if (locales.isEmpty()) return null;
        return locales.get(0);
    }

    public List<Locale> getLocales() {
        return _locales == null ? Collections.<Locale> emptyList() : _locales;
    }

    public String getMethod() {
        return _method;
    }

    public Map<String, List<String>> getParameters() {
        if (_query == null) return Collections.emptyMap();

        Map<String, List<String>> map = new HashMap<>();
        String[] parameters = _query.split("&");
        for (String keyValuePairs : parameters) {
            try {
                String[] keyValues = keyValuePairs.split("=");
                String k = URLDecoder.decode(keyValues[0], CharEncoding.UTF_8);
                String v = keyValues.length <= 0 ? null : URLDecoder.decode(keyValues[1], CharEncoding.UTF_8);

                List<String> values = map.get(k);
                if (values == null) map.put(k, (values = new ArrayList<>()));
                values.add(v);
            } catch (UnsupportedEncodingException ignore) {}
        }

        return map;
    }

    public int getPort() {
        return _port;
    }

    public String getQuery() {
        return _query;
    }

    public String getReferer() {
        return getHeader("referer");
    }

    public String getRemoteUser() {
        return _remoteUser;
    }

    public String getScheme() {
        return _scheme;
    }

    public String getServerName() {
        return _serverName;
    }

    public String getUri() {
        return _uri;
    }

    public String getUrl() {
        StringBuilder url = new StringBuilder();
        if (_scheme != null) url.append(_scheme).append("://");
        if (_serverName != null) url.append(_serverName);
        if (_port != 80) url.append(":").append(_port);
        if (_uri != null) url.append(_uri);
        if (_query != null) url.append("?").append(_query);
        return url.toString();
    }

    public String getUserAgent() {
        return getHeader("user-agent");
    }
}
