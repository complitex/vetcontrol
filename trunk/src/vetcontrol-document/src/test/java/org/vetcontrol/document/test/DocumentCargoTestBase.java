package org.vetcontrol.document.test;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.Vehicle;

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
    protected static final String WAIT_FOR_PAGE_TO_LOAD = "60000";
    protected static final int AJAX_WAIT = 2000;
    protected static final int MAX_CARGO = 20;
    protected static final int MAX_VEHICLE = 10;

    protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

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

    /**
     * Заполнение полей документа
     * @param documentCargo карточка на груз
     * @param isNew true если новый, false если редактируется
     * @throws Exception Exception
     */
    protected void inputDocumentCargo(DocumentCargo documentCargo, boolean isNew) throws Exception{
        //  Документ
        session().select("name=document.cargo.movement_type:select", "value=" + documentCargo.getMovementType().name());
        session().select("name=document.cargo.vehicle_type:select", "value=" + documentCargo.getVehicleType().name());
        session().select("name=document.cargo.cargo_sender_country", "value=" + documentCargo.getSenderCountry().getId());
        session().type("name=document.cargo.cargo_sender_name", documentCargo.getSenderName());
        session().type("name=document.cargo.cargo_receiver_name", documentCargo.getReceiverName());
        waitForAjaxUpdate();
        session().type("name=document.cargo.cargo_receiver_address", documentCargo.getReceiverAddress());
        session().type("name=document.cargo.details", documentCargo.getDetails());

        //  Транспортное средство
        for (int i = 0; i< documentCargo.getVehicles().size(); ++i){
            String detailsLocator = "name=document.cargo.vehicle_container:document.cargo.vehicle_list:" + i + ":document.cargo.vehicle.details";

            if (i > 0 && !session().isElementPresent(detailsLocator)){
                session().click("xpath=//a[contains(@id,'document_cargo_vehicle_add')]");
                waitForElementPresent(detailsLocator);
            }

            session().type(detailsLocator, documentCargo.getVehicles().get(i).getVehicleDetails());
            waitForAjaxUpdate();
        }

        //Грузы
        for (int i=0; i < documentCargo.getCargos().size(); ++i){
            Cargo cargo = documentCargo.getCargos().get(i);

            String cargoLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.cargo_type:uktzed";

            if (i > 0 && !session().isElementPresent(cargoLocator)) {
                session().click("xpath=//a[contains(@id,'document_cargo_cargo_add')]");

                waitForElementPresent(cargoLocator);
            }

            //  тип груза
            String typeLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.cargo_type:uktzed";
            session().type(typeLocator, cargo.getCargoType().getCode());

            String unitTypeLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.unit_type";

            //  выбор вида груза
            if (i == 0 && DocumentCargoFactory.getCargoModeCount(cargo.getCargoType()) > 1) {
                waitForAjaxUpdate();

                String cargoModeLocator = "name=document.cargo.cargo_mode_container:document.cargo.cargo_mode_parent_radio_group";
                waitForElementPresent(cargoModeLocator);

                session().click(cargoModeLocator + " value=" + documentCargo.getCargoMode().getId());

                waitForSelectAjaxUpdate(unitTypeLocator);
            }

            waitForAjaxUpdate();

            //  количество
            String countLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.count";
            session().type(countLocator, String.valueOf(cargo.getCount()).replace('.',','));

            //  единицы измерения
            waitForAjaxUpdate();
            session().select(unitTypeLocator, "value=" + cargo.getUnitType().getId());

            // транспорт
            String vehicleLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.vehicle";
            session().select(vehicleLocator, "value=" + documentCargo.getVehicles().indexOf(cargo.getVehicle()));

            //  производитель
            String countyLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.producer_country";
            session().select(countyLocator, "value=" + cargo.getCargoProducer().getCountry().getId());

            waitForAjaxUpdate();

            String producerLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.producer_name";
            session().select(producerLocator, "value=" + cargo.getCargoProducer().getId());

            //  сертификат
            String detailLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.certificate_detail";
            session().type(detailLocator, cargo.getCertificateDetails());

            String dateLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:"+i+":document.cargo.certificate_date";
            session().type(dateLocator, simpleDateFormat.format(cargo.getCertificateDate()));

            session().click(typeLocator);
        }

        if (!isNew) { //редактирование
            //удаление неиспользуемых грузов
            int cargoSize = documentCargo.getCargos().size();

            for (int i = cargoSize; i < MAX_CARGO ; ++i){
                String cargoLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + cargoSize + ":document.cargo.cargo_type:uktzed";

                if (session().isElementPresent(cargoLocator)){
                    session().click("xpath=//a[contains(@id,'document_cargo_delete') and @name='delete:"+i+"']");
                    waitForAjaxUpdate();
                }else{
                    break;
                }
            }

            //удаление неиспользуемых транспортных средств
            int vehicleSize = documentCargo.getVehicles().size();

            for (int i = vehicleSize; i < MAX_VEHICLE; ++i){
                String detailsLocator = "name=document.cargo.vehicle_container:document.cargo.vehicle_list:" + vehicleSize + ":document.cargo.vehicle.details";                

                if (session().isElementPresent(detailsLocator)){
                    session().click("xpath=//a[contains(@id,'document_cargo_vehicle_delete') and @name='delete:" + vehicleSize + "']");
                    waitForAjaxUpdate();
                }else{
                    break;
                }
            }
        }
    }

    /**
     * Проверка идентичности документа в сохраненным по уникальному значению в примечании
     * @param documentCargo новый документ
     */
    public void assertEqualToSaved(DocumentCargo documentCargo){
        DocumentCargo savedDocumentCargo = DocumentCargoFactory.getDocumentCargo(documentCargo.getDetails());

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

        assert documentCargo.equals(savedDocumentCargo);
    }
}
