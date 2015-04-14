package org.storm.clickstream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.storm.clickstream.utils.FileSystemUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Loads the clickstream configuration from either classpath:clickstream.xml or META-INF/clickstream.xml.
 * 
 * @author <a href="mailto:timothy.storm@fedex.com">Timothy Storm</a>
 */
class CSConfigLoader {
    /**
     * SAX Handler implementation for handling tags in config file and building config objects.
     */
    private class ConfigHandler extends DefaultHandler {
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("bot-host")) _config.addBotHost(attributes.getValue("name"));
            else if (qName.equals("bot-agent")) _config.addBotAgent(attributes.getValue("name"));
        }
    }

    private static class Resource implements Serializable {
        private static final long serialVersionUID = ClickStreamVersion.SVUID;
        private final Class<?>    _clazz;
        private final String      _path;

        Resource(String path) {
            this(path, null);
        }

        Resource(String path, Class<?> clazz) {
            String pathToUse = FilenameUtils.normalize(path, true);
            if (pathToUse.startsWith("/")) pathToUse = pathToUse.substring(1);
            _path = path;
            _clazz = clazz;
        }

        public boolean exists() {
            return resolveURL() != null;
        }

        public String getFilename() {
            return FilenameUtils.getName(_path);
        }

        public InputStream getInputStream() throws IOException {
            InputStream is = null;
            if (_clazz != null) is = _clazz.getResourceAsStream(_path);
            if (is == null) ClassLoader.getSystemResource(_path);
            if (is == null) {
                URL url = resolveURL();
                if (url != null) is = url.openStream();
            }
            return is;
        }

        /**
         * Resolves a URL for the underlying class path resource.
         * 
         * @return the resolved URL, or {@code null} if not resolvable
         */
        private URL resolveURL() {
            if (_clazz != null) return _clazz.getResource(_path);

            // search classpath
            try {
                String classpath = System.getProperty("java.class.path");
                if (classpath == null) return null;

                String[] paths = classpath.split(File.pathSeparator);
                File file = searchDirectories(paths, getFilename());
                if (file != null) return file.toURI().toURL();
            } catch (IOException e) {}

            // search loader
            try {
                File file = searchLoader(_path);
                if (file != null) return file.toURI().toURL();
            } catch (IOException e) {}
            return null;
        }

        private File searchDirectories(String[] paths, String name) throws IOException {
            for (String path : paths) {
                File file = FileSystemUtils.discover(path, name);
                if (file != null) return file;
            }
            return null;
        }

        private File searchLoader(String name) throws IOException {
            String nm = name;
            if (!nm.startsWith("/")) nm = "/" + nm;
            URL url = Resource.class.getResource(nm);
            if (url == null) return null;

            String external = url.toExternalForm();
            if (external.startsWith("file:")) return new File(external.substring(5));
            return null;
        }
    }

    private static CSConfigLoader _singleton;

    public static CSConfigLoader getInstance() {
        if (_singleton == null) {
            synchronized (CSConfigLoader.class) {
                if (_singleton == null) {
                    _singleton = new CSConfigLoader();
                }
            }
        }
        return _singleton;
    }

    private CSConfig _config;

    private CSConfigLoader() {}

    public CSConfig getConfig() {
        if (_config == null) {
            synchronized (CSConfigLoader.class) {
                if (_config == null) {
                    _config = new CSConfig();

                    // attempt to load the clickstream configuration(s)
                    Resource resource = new Resource("clickstream.xml");
                    if (!resource.exists()) resource = new Resource("clickstream-default.xml");
                    if (!resource.exists()) return _config;

                    try (InputStream is = resource.getInputStream()) {
                        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                        parser.parse(is, new ConfigHandler());
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }
        return _config;
    }
}
