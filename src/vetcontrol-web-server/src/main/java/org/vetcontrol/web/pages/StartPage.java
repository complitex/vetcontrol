/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.web.pages;

import java.util.Arrays;
import java.util.Locale;
import org.apache.wicket.markup.html.WebPage;
import org.vetcontrol.web.components.LocalePicker;

/**
 *
 * @author Artem
 */
public class StartPage extends WebPage {

    public StartPage() {
        add(new LocalePicker("localePicker", Arrays.asList(new Locale[]{Locale.ENGLISH, new Locale("ru")})));
    }
}
