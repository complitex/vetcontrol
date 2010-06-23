package org.vetcontrol.document.test;

import org.testng.annotations.Test;
import org.vetcontrol.entity.DocumentCargo;

import java.util.Random;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.06.2010 13:22:16
 */
public class DocumentCargoTest extends DocumentCargoTestBase {
    private static int MAX_ITEM_PER_PAGE = 10;
    private static Random random = new Random();

    /**
     * Тест создания нового документа
     * @throws Exception Exception
     */
    @Test(threadPoolSize = 2, invocationCount = 5)
    public void testDocumentCargoCreate() throws Exception {
        //Авторизация
        login(LOGIN, PASSWORD);

        //Страница списка карточек на груз
        session().click("id=DocumentCargoList");
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Страница добавления документа
        session().click("id=AddDocumentButton");
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        DocumentCargo documentCargo = DocumentCargoFactory.createRandomDocument(LOGIN, "new");
        inputDocumentCargo(documentCargo, true);

        session().click("name=save");

        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        assertEqualToSaved(documentCargo);
    }

    /**
     * Тест редактирования документа
     * @throws Exception Exception
     */
    @Test(threadPoolSize = 2, invocationCount = 5)
    public void testDocumentCargoEdit() throws Exception{
         //Авторизация
        login(LOGIN, PASSWORD);

        //Страница списка карточек на груз
        session().click("id=DocumentCargoList");
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Страница редактирования случайного документа
        session().click("name=edit:" + (random.nextInt(MAX_ITEM_PER_PAGE) + 1));
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        DocumentCargo documentCargo = DocumentCargoFactory.createRandomDocument(LOGIN, "edit");
        inputDocumentCargo(documentCargo, false);

        session().click("name=save");

        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        assertEqualToSaved(documentCargo);                
    }

}
