/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.commons.util;

import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class SqlQueryLoader {

    private static final String XML_FILE_EXTENSION = ".xml";
    private static final Logger log = LoggerFactory.getLogger(SqlQueryLoader.class);

    private SqlQueryLoader() {
    }

    public static String getQuery(Class<?> entityClass, String key) {
        InputStream inputStream = null;
        try {
            inputStream = entityClass.getResourceAsStream(entityClass.getSimpleName() + XML_FILE_EXTENSION);
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            return properties.getProperty(key).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("Can't to close input stream.", e);
                }
            }
        }
    }
}
