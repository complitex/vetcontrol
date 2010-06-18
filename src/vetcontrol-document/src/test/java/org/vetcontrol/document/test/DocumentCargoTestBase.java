package org.vetcontrol.document.test;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.text.SimpleDateFormat;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.06.2010 18:47:49
 */
public class DocumentCargoTestBase {
    protected static final String SERVER_URL = "http://localhost:8080/server/";
    protected static final String LOGIN = "login_2";
    protected static final String PASSWORD = "login_2";
    protected static final String WAIT_FOR_PAGE_TO_LOAD = "15000";
    protected static final int AJAX_WAIT = 2000;
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() throws Exception {
        startSeleniumSession("localhost", 4444, "*firefox", SERVER_URL);

        session().setTimeout("60000");
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() throws Exception {
        closeSeleniumSession();
    }

    protected void login(String login, String password){
        session().open(SERVER_URL + "?wicket:bookmarkablePage=:org.vetcontrol.web.pages.login.Login");
        session().type("name=j_username", login);
        session().type("name=j_password", password);
        session().click("name=submit");
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);
    }

    protected void waitForSelectAjaxUpdate(String selectLocator) throws InterruptedException {
        for (int i=0; i<60; ++i){
            String[] s = session().getSelectOptions(selectLocator);
            if (s.length < 2){
                Thread.sleep(AJAX_WAIT);
            }else{
                break;
            }
        }
    }

    protected void waitForAjaxUpdate() throws InterruptedException {
        Thread.sleep(AJAX_WAIT);
    }

    protected void waitForElementPresent(String locator){
        session().waitForCondition("selenium.isElementPresent('" + locator + "')", "60000");
    }
}
