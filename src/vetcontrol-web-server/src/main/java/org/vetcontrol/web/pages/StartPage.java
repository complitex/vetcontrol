/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages;

import java.util.Arrays;
import java.util.Locale;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.web.components.LocalePicker;

/**
 *
 * @author Artem
 */
public class StartPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(StartPage.class);

    public StartPage() {

        log.info("Start application");

        add(new LocalePicker("localePicker", Arrays.asList(new Locale[]{Locale.ENGLISH, new Locale("ru")})));
    }
}
