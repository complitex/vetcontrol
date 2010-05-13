/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.vetcontrol.book;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class BookHash implements Serializable {

    private Integer bookHash;
    private Map<String, Map<String, Integer>> stringCultureHashes = new HashMap<String, Map<String, Integer>>();

    public BookHash() {
    }

    public Integer getBookHash() {
        return bookHash;
    }

    public void setBookHash(Integer bookHash) {
        this.bookHash = bookHash;
    }

    public Map<String, Map<String, Integer>> getStringCultureHashes() {
        return stringCultureHashes;
    }

    public void setStringCultureHashes(Map<String, Map<String, Integer>> stringCultureHashes) {
        this.stringCultureHashes = stringCultureHashes;
    }

    public void addStringCultureHash(String propertyName, Map<String, Integer> localeToHashMap){
        this.stringCultureHashes.put(propertyName, localeToHashMap);
    }

    public void addStringCultureHash(String propertyName, String locale, Integer hash){
        Map<String, Integer> localeToHashMap = stringCultureHashes.get(propertyName);
        if(localeToHashMap == null){
            localeToHashMap = new HashMap<String, Integer>();
            stringCultureHashes.put(propertyName, localeToHashMap);
        }
        localeToHashMap.put(locale, hash);
    }
}
