package org.vetcontrol.document.test;

import com.thoughtworks.selenium.SeleneseTestCase;
import org.vetcontrol.entity.MovementType;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.06.2010 13:22:16
 */
public class DocumentCargoTest extends SeleneseTestCase {
    protected static String SERVER_URL = "http://localhost:8080/server/";
    protected static String WAIT_FOR_PAGE_TO_LOAD = "15000";
    private Random random = new Random();

    public void setUp() throws Exception {
        setUp(SERVER_URL, "*firefox F:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
        selenium.setTimeout("60000");
    }

    protected void login(String login, String password){
        selenium.open(SERVER_URL + "?wicket:bookmarkablePage=:org.vetcontrol.web.pages.login.Login");        
        selenium.type("name=j_username", login);
        selenium.type("name=j_password", password);
        selenium.click("name=submit");
        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);
    }

    protected String getRandomString(){
        return UUID.randomUUID().toString();
    }

    protected int getRandomInt(int n){
        return random.nextInt(n);
    }

    protected boolean getRandomBoolean(){
        return random.nextBoolean();
    }

    protected double getRandomDouble(){
        return random.nextDouble();
    }

    public void testDocumentCargoCreate() throws Exception {

        //Авторизация
        login("login_2", "login_2");

        //Страница списка карточек на груз
        selenium.click("id=DocumentCargoList");
        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Страница добавления документа
        selenium.click("id=AddDocumentButton");
        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Заполнение полей

        //  Документ
        String movementType = getRandomBoolean() ? MovementType.IMPORT.name() : MovementType.TRANSIT.name();
        selenium.select("name=document.cargo.movement_type:select", "value="+movementType);

        selenium.select("name=document.cargo.vehicle_type", "value=CAR");

        selectRandom("name=document.cargo.cargo_sender_country");
        
        selenium.type("name=document.cargo.cargo_sender_name", "sender_name::" + getRandomString());

        selenium.type("name=document.cargo.cargo_receiver_name", "receiver_name::" + getRandomString());
        selenium.type("name=document.cargo.cargo_receiver_address", "receiver_address::" + getRandomString());

        selenium.type("name=document.cargo.details", "cargo.details::" + getRandomString());

        //selenium.waitForCondition("selenium.isElementPresent(\""+ "LoginTextBox" +"\")", "10000");
              
        //  Транспортное средство
        int vehicleCount = getRandomInt(9) + 1;
        for (int i = 0; i< vehicleCount; ++i){
            selenium.click("xpath=//a[contains(@id,'document_cargo_vehicle_add')]");

            String detailsLocator = "name=document.cargo.vehicle_container:document.cargo.vehicle_list:"+ i +":document.cargo.vehicle.details";
            selenium.waitForCondition("selenium.isElementPresent('" + detailsLocator + "')", "60000");
            selenium.type(detailsLocator, "vehicle.details::" + i + "::" + getRandomString());
        }

        //Грузы
        int cargoCount = getRandomInt(9) + 1;

        addCargo(0);
        String cargoModeLocator = "name=document.cargo.cargo_mode_container:document.cargo.cargo_mode_parent_radio_group";
        selenium.waitForCondition("selenium.isElementPresent('" + cargoModeLocator + "')", "60000");
        selenium.click(cargoModeLocator + " index=0");

        for (int i=0; i < cargoCount; ++i){
            selenium.click("xpath=//a[contains(@id,'document_cargo_cargo_add')]");

            String cargoLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.cargo_type:uktzed";

            selenium.waitForCondition("selenium.isElementPresent('" + cargoLocator + "')", "60000");
            addCargo(i);
        }

        Thread.sleep(30000);
    }

    private void addCargo(int index){
        selenium.type("name=document.cargo.cargo_container:document.cargo.cargo_list:" + index + ":document.cargo.cargo_type:uktzed",
                "2602000000");
        selenium.type("name=document.cargo.cargo_container:document.cargo.cargo_list:" + index + ":document.cargo.count",
                String.valueOf(getRandomDouble()*1000));

//        selectRandom("name=document.cargo.cargo_container:document.cargo.cargo_list:" + index + ":document.cargo.unit_type");
        selectRandom("name=document.cargo.cargo_container:document.cargo.cargo_list:" + index + ":document.cargo.vehicle");
    }

    private void selectRandom(String selectLocator){
        String[] options = selenium.getSelectOptions(selectLocator);

        System.out.println(Arrays.toString(options));

        selenium.select(selectLocator, "label=" + options[getRandomInt(options.length-1)+1]);
    }
}
