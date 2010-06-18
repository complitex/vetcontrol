package org.vetcontrol.document.test;

import org.testng.annotations.Test;
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.Vehicle;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.06.2010 13:22:16
 */
public class DocumentCargoTest extends DocumentCargoTestBase {

    @Test(threadPoolSize = 2, invocationCount = 10)
    public void testDocumentCargoCreate() throws Exception {
        DocumentCargo documentCargo = DocumentCargoFactory.createRandomDocument(LOGIN);

        //Авторизация
        login(LOGIN, PASSWORD);

        //Страница списка карточек на груз
        session().click("id=DocumentCargoList");
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Страница добавления документа
        session().click("id=AddDocumentButton");
        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

        //Заполнение полей

        //  Документ
        session().select("name=document.cargo.movement_type:select", "value=" + documentCargo.getMovementType().name());
        session().select("name=document.cargo.vehicle_type", "value=" + documentCargo.getVehicleType().name());
        session().select("name=document.cargo.cargo_sender_country", "value=" + documentCargo.getSenderCountry().getId());
        session().type("name=document.cargo.cargo_sender_name", documentCargo.getSenderName());
        session().type("name=document.cargo.cargo_receiver_name", documentCargo.getReceiverName());
        waitForAjaxUpdate();
        session().type("name=document.cargo.cargo_receiver_address", documentCargo.getReceiverAddress());
        session().type("name=document.cargo.details", documentCargo.getDetails());

        //  Транспортное средство
        for (int i = 0; i< documentCargo.getVehicles().size(); ++i){
            String detailsLocator = "name=document.cargo.vehicle_container:document.cargo.vehicle_list:" + i + ":document.cargo.vehicle.details";

            if (i > 0){
                session().click("xpath=//a[contains(@id,'document_cargo_vehicle_add')]");
                waitForElementPresent(detailsLocator);
            }

            session().type(detailsLocator, documentCargo.getVehicles().get(i).getVehicleDetails());
        }

        //Грузы
        for (int i=0; i < documentCargo.getCargos().size(); ++i){
            Cargo cargo = documentCargo.getCargos().get(i);

            if (i > 0) {
                session().click("xpath=//a[contains(@id,'document_cargo_cargo_add')]");

                String cargoLocator = "name=document.cargo.cargo_container:document.cargo.cargo_list:" + i + ":document.cargo.cargo_type:uktzed";
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
            session().select(vehicleLocator, "value=" + documentCargo.getVehicles().indexOf(cargo.getVehicle()) + ":null");

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

        session().click("name=save");

        session().waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD);

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

        assert documentCargo.equals(savedDocumentCargo);
    }

}
