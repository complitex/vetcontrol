/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.report.util;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Artem
 */
public class QueryLoader {

    private static final String extension = ".xml";

    public static String getQuery(Class<?> entity, String key) {
        try {
            InputStream is = entity.getResourceAsStream(entity.getSimpleName() + extension);
            Properties props = new Properties();
            props.loadFromXML(is);
            return props.getProperty(key).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
