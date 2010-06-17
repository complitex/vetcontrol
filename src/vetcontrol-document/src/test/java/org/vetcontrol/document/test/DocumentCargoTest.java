package org.vetcontrol.document.test;

import com.thoughtworks.selenium.SeleneseTestCase;
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.Vehicle;

import java.text.SimpleDateFormat;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.06.2010 13:22:16
 */
public class DocumentCargoTest extends SeleneseTestCase {

    protected static String SERVER_URL = "http://localhost:8080/server/";
    protected static String LOGIN = "login_2";
    protected static String PASSWORD = "login_2";

    protected static String WAIT_FOR_PAGE_TO_LOAD = "15000";
    protected static int AJAX_WAIT = 3000;
       
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public void setUp() throws Exception {
        setUp(SERVER_URL, "*firefox3");
        selenium.setTimeout("60000");
    }

    protected void login(String login, String password){
        selenium.open(SERVER_URL + "?wicket:bookmarkablePage=:org.vetcontrol.web.pages.login.Login");
        selenium.type("name=j_username", login);
        selenium.type("name=j_password", password);
        selenium.click("name=submit");
        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);
    }

    public void testDocumentCargoCreate() throws Exception {
        DocumentCargo documentCargo = DocumentCargoFactory.createRandomDocument(LOGIN);

        //Авторизация
        login(LOGIN, PASSWORD);

        //Страница списка карточек на груз
        selenium.click("id=DocumentCargoList");
        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Страница добавления документа
        selenium.click("id=AddDocumentButton");
        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Заполнение полей

        //  Документ
        selenium.select("name=document.cargo.movement_type:select", "value=" + documentCargo.getMovementType().name());
        selenium.select("name=document.cargo.vehicle_type", "value=" + documentCargo.getVehicleType().name());
        selenium.select("name=document.cargo.cargo_sender_country", "value=" + documentCargo.getSenderCountry().getId());
        selenium.type("name=document.cargo.cargo_sender_name", documentCargo.getSenderName());        
        selenium.type("name=document.cargo.cargo_receiver_name", documentCargo.getReceiverName());
        waitForAjaxUpdate();
        selenium.type("name=document.cargo.cargo_receiver_address", documentCargo.getReceiverAddress());
        selenium.type("name=document.cargo.details", documentCargo.getDetails());

        //  Транспортное средство
        for (int i = 0; i< documentCargo.getVehicles().size(); ++i){
            String detailsLocator = "name=document.cargo.vehicle_container:document.cargo.vehicle_list:" + i + ":document.cargo.vehicle.details";

            if (i > 0){
                selenium.click("xpath=//a[contains(@id,'document_cargo_vehicle_add')]");
                waitForElementPresent(detailsLocator);
            }

            selenium.type(detailsLocator, documentCargo.getVehicles().get(i).getVehicleDetails());
        }

        //Грузы
        for (int i=0; i < documentCargo.getCargos().size(); ++i){
            Cargo cargo = documentCargo.getCargos().get(i);

            if (i > 0) {
                selenium.click("xpath=//a[contains(@id,'document_cargo_cargo_add')]");

                String cargoLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.cargo_type:uktzed";
                waitForElementPresent(cargoLocator);
            }

            //  тип груза
            String typeLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.cargo_type:uktzed";
            selenium.type(typeLocator, cargo.getCargoType().getCode());

            String unitTypeLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.unit_type";

            //  выбор вида груза
            if (i == 0) {
                waitForAjaxUpdate();

                String cargoModeLocator = "name=document.cargo.cargo_mode_container:document.cargo.cargo_mode_parent_radio_group";
                waitForElementPresent(cargoModeLocator);

                selenium.click(cargoModeLocator + " value=" + documentCargo.getCargoMode().getId());

                waitForSelectAjaxUpdate(unitTypeLocator);
            }

            //  количество
            String countLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.count";
            selenium.type(countLocator, String.valueOf(cargo.getCount()).replace('.',','));

            //  единицы измерения
            waitForAjaxUpdate();
            selenium.select(unitTypeLocator, "value=" + cargo.getUnitType().getId());

            // транспорт
            String vehicleLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.vehicle"; 
            selenium.select(vehicleLocator, "value=" + documentCargo.getVehicles().indexOf(cargo.getVehicle()) + ":null");

            //  производитель
            String countyLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.producer_country";
            selenium.select(countyLocator, "value=" + cargo.getCargoProducer().getCountry().getId());

            waitForAjaxUpdate();

            String producerLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.producer_name";
            selenium.select(producerLocator, "value=" + cargo.getCargoProducer().getId());

            //  сертификат
            String detailLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.certificate_detail";
            selenium.type(detailLocator, cargo.getCertificateDetails());

            String dateLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.certificate_date";
            selenium.type(dateLocator, simpleDateFormat.format(cargo.getCertificateDate()));

            selenium.click(typeLocator);
        }

        selenium.click("name=save");

        selenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        DocumentCargo savedDocumentCargo = DocumentCargoFactory.getDocumentCargo(documentCargo.getDetails());

        //Проверка идентичности
        documentCargo.setId(savedDocumentCargo.getId());
        documentCargo.setCreated(savedDocumentCargo.getCreated());
        documentCargo.setUpdated(savedDocumentCargo.getUpdated());

        for (Vehicle vehicle : documentCargo.getVehicles()){
            for (Vehicle saved : savedDocumentCargo.getVehicles()){
                if (vehicle.getVehicleDetails().equals(saved.getVehicleDetails())){
                    vehicle.setId(saved.getId());
                    vehicle.setDocumentCargoId(saved.getDocumentCargoId());
                    vehicle.setDocumentCargo(saved.getDocumentCargo()); //avoid cyclic equal check
                    vehicle.setVehicleType(saved.getVehicleType());
                    vehicle.setUpdated(saved.getUpdated());
                }
            }
        }

        for (Cargo cargo : documentCargo.getCargos()){
            for (Cargo saved : savedDocumentCargo.getCargos()){
                if (cargo.getCertificateDetails().equals(saved.getCertificateDetails())){
                    cargo.setId(saved.getId());
                    cargo.setDocumentCargoId(saved.getDocumentCargoId());
                    cargo.setVehicleId(saved.getVehicleId());
                    cargo.setDocumentCargo(saved.getDocumentCargo()); //avoid cyclic equal check
                    cargo.setUpdated(saved.getUpdated());                    
                }
            }
        }

        assertTrue("documents equals", documentCargo.equals(savedDocumentCargo));
    }

    private void waitForSelectAjaxUpdate(String selectLocator) throws InterruptedException {
        for (int i=0; i<60; ++i){
            String[] s = selenium.getSelectOptions(selectLocator);
            if (s.length < 2){
                Thread.sleep(AJAX_WAIT);
            }else{
                break;
            }
        }
    }

    private void waitForAjaxUpdate() throws InterruptedException {
        Thread.sleep(AJAX_WAIT);
    }

    private void waitForElementPresent(String locator){
        selenium.waitForCondition("selenium.isElementPresent('" + locator + "')", "60000");
    }
}
