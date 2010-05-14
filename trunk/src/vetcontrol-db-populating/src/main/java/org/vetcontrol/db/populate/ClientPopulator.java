/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.db.populate;

import org.vetcontrol.db.populate.util.GenerateUtil;
import org.vetcontrol.entity.*;
import org.vetcontrol.entity.Synchronized.SyncStatus;
import org.vetcontrol.service.ClientBean;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class ClientPopulator extends AbstractPopulator {

    /*--------------------------Settings------------------------------------------------------*/
    private static final String CLIENT_PERSISTENCE_UNIT_NAME = "populate.client";

    //count of cargo documents to be generated
    private static final int DOCUMENT_CARGO_COUNT = 10;

    //count of arrest documents to be generated
    private static final int ARREST_DOCUMENT_COUNT = 10;

    //count of cargos for one DocumentCargo entry.
    private static final int CARGO_COUNT = 5;

    //Department id of creator of documents.
    private static final Long DEPARTMENT_ID = 3L;

    //User id of document's creator.
    private static final Long CREATOR_ID = 20L;

    //Vehicle type
    private static final VehicleType VEHICLE_TYPE = VehicleType.CAR;
    /*--------------------------- End settings -----------------------------------------------*/

    private static final Logger log = LoggerFactory.getLogger(ClientPopulator.class);

    private ClientBean clientBean;

    private Department department;

    private User creator;

    private Client client;

    private void initClient() {
        clientBean = new ClientBean();
        clientBean.setEntityManager(getEntityManager());
        if (department == null) {
            department = getEntityManager().getReference(Department.class, DEPARTMENT_ID);
        }
        if (creator == null) {
            creator = getEntityManager().getReference(User.class, CREATOR_ID);
        }
        if (client == null) {
            client = clientBean.getCurrentClient();
        }
    }

    private void populateDocumentCargo(Date updated) {
        CargoMode cargoMode = findCargoMode();

        DocumentCargo documentCargo = newDocumentCargo(cargoMode, updated);

        //save document cargo.
        documentCargo = getEntityManager().merge(documentCargo);

        List<CargoType> cargoTypes = findCargoTypes(cargoMode);
        for (CargoType cargoType : cargoTypes) {
            //save vehicle
            Vehicle vehicle = newVehicle(documentCargo, updated);
            vehicle = getEntityManager().merge(vehicle);

            //save cargo
            UnitType unitType = findUnitType(cargoMode);
            Cargo cargo = newCargo(documentCargo, cargoType, unitType, vehicle.getId(), updated);
            getEntityManager().merge(cargo);
        }
    }

    private CargoMode findCargoMode() {
        String size = "SELECT COUNT(DISTINCT cm.id) FROM CargoMode cm JOIN cm.cargoModeCargoTypes cmct JOIN cm.cargoModeUnitTypes cmut";
        int count = getEntityManager().createQuery(size, Long.class).getSingleResult().intValue();
        if (count == 0) {
            throw new RuntimeException("There are no cargo modes.");
        }
        String query = "SELECT DISTINCT cm FROM CargoMode cm JOIN cm.cargoModeCargoTypes cmct JOIN cm.cargoModeUnitTypes cmut";
        CargoMode cargoMode = getEntityManager().createQuery(query, CargoMode.class).
                setFirstResult(GenerateUtil.generateInt(count - 1)).setMaxResults(1).getResultList().get(0);
        return cargoMode;
    }

    private List<CargoType> findCargoTypes(CargoMode cargoMode) {
        String query = "SELECT DISTINCT cmct.cargoType FROM CargoModeCargoType cmct WHERE cmct.cargoMode = :cargoMode";
        List<CargoType> cargoTypes = getEntityManager().createQuery(query, CargoType.class).
                setParameter("cargoMode", cargoMode).
                setFirstResult(0).setMaxResults(CARGO_COUNT).getResultList();
        return cargoTypes;
    }

    private UnitType findUnitType(CargoMode cargoMode) {
        String size = "SELECT COUNT(DISTINCT cmut.unitType.id) FROM CargoModeUnitType cmut WHERE cmut.cargoMode = :cargoMode";
        int count = getEntityManager().createQuery(size, Long.class).
                setParameter("cargoMode", cargoMode).getSingleResult().intValue();
        if (count == 0) {
            throw new RuntimeException("There are no unit types for given cargo mode.");
        }
        String query = "SELECT DISTINCT cmut.unitType FROM CargoModeUnitType cmut WHERE cmut.cargoMode = :cargoMode";
        UnitType unitType = getEntityManager().createQuery(query, UnitType.class).
                setParameter("cargoMode", cargoMode).
                setFirstResult(GenerateUtil.generateInt(count - 1)).setMaxResults(1).getResultList().get(0);
        return unitType;
    }

    private DocumentCargo newDocumentCargo(CargoMode cargoMode, Date updated) {
        DocumentCargo documentCargo = new DocumentCargo();
        documentCargo.setDepartment(department);
        documentCargo.setCreator(creator);
        documentCargo.setClient(client);

        documentCargo.setCreated(updated);
        documentCargo.setUpdated(updated);
        documentCargo.setMovementType(MovementType.IMPORT);
        documentCargo.setPassingBorderPoint(findAny(PassingBorderPoint.class));
        documentCargo.setVehicleType(VEHICLE_TYPE);
        documentCargo.setDetails(GenerateUtil.generateString(255));
        documentCargo.setDetentionDetails(GenerateUtil.generateString(255));
        documentCargo.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        documentCargo.setReceiverAddress(GenerateUtil.generateString(100));
        documentCargo.setReceiverName(GenerateUtil.generateString(100));
        documentCargo.setSenderName(GenerateUtil.generateString(100));
        documentCargo.setSenderCountry(findAny(CountryBook.class));
        documentCargo.setCargoMode(cargoMode);
        return documentCargo;
    }

    private Vehicle newVehicle(DocumentCargo documentCargo, Date updated) {
        Vehicle vehicle = new Vehicle();
        vehicle.setClient(client);
        vehicle.setDepartment(department);
        vehicle.setDocumentCargo(documentCargo);
        vehicle.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        vehicle.setUpdated(updated);
        vehicle.setVehicleDetails(GenerateUtil.generateString(255));
        vehicle.setVehicleType(VEHICLE_TYPE);
        return vehicle;
    }

    private Cargo newCargo(DocumentCargo documentCargo, CargoType cargoType, UnitType unitType, Long vehicleId, Date updated) {
        Cargo cargo = new Cargo();
        cargo.setCertificateDate(new Date());
        cargo.setCertificateDetails(GenerateUtil.generateString(255));
        cargo.setClient(client);
        cargo.setCount(GenerateUtil.generateDouble(1000));
        cargo.setDepartment(department);
        cargo.setDocumentCargo(documentCargo);
        cargo.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        cargo.setCargoProducer(findAny(CargoProducer.class));
        cargo.setUpdated(updated);
        cargo.setCargoType(cargoType);
        cargo.setUnitType(unitType);
        cargo.setVehicleId(vehicleId);
        return cargo;
    }

    @Override
    protected void populate() {
        Date now = new Date();

        //document cargos
        log.info("Start populate {}", DocumentCargo.class.getSimpleName());
        for (int i = 0; i < DOCUMENT_CARGO_COUNT; i++) {
            startTransaction();
            initClient();
            populateDocumentCargo(now);
            endTransaction();
        }
        log.info("End populate {}", DocumentCargo.class.getSimpleName());

        //arrest documents
        log.info("Start populate {}", ArrestDocument.class.getSimpleName());
        for (int i = 0; i < ARREST_DOCUMENT_COUNT; i++) {
            startTransaction();
            initClient();
            populateArrestDocument(now);
            endTransaction();
        }
        log.info("End populate {}", ArrestDocument.class.getSimpleName());
    }

    @Override
    protected String getPersistenceUnitName() {
        return CLIENT_PERSISTENCE_UNIT_NAME;
    }

    public static void main(String[] args) {
        new ClientPopulator().populate();
    }

    private void populateArrestDocument(Date updated) {
        ArrestDocument arrestDocument = newArrestDocument(updated);
        getEntityManager().merge(arrestDocument);
    }

    private ArrestDocument newArrestDocument(Date updated) {
        ArrestDocument arrestDocument = new ArrestDocument();
        arrestDocument.setArrestDate(updated);
        arrestDocument.setUpdated(updated);
        arrestDocument.setClient(client);
        arrestDocument.setCreator(creator);
        arrestDocument.setDepartment(department);
        arrestDocument.setArrestReason(findAny(ArrestReason.class));
        arrestDocument.setArrestReasonDetails(GenerateUtil.generateString(255));
        arrestDocument.setCargoMode(findCargoMode());
        arrestDocument.setCargoType(findCargoTypes(arrestDocument.getCargoMode()).get(0));
        arrestDocument.setCertificateDate(GenerateUtil.generateDate());
        arrestDocument.setCertificateDetails(GenerateUtil.generateString(255));
        arrestDocument.setCount(GenerateUtil.generateDouble(1000));
        arrestDocument.setDocumentCargoCreated(updated);
        arrestDocument.setPassingBorderPoint(findAny(PassingBorderPoint.class));
        arrestDocument.setReceiverAddress(GenerateUtil.generateString(100));
        arrestDocument.setReceiverName(GenerateUtil.generateString(100));
        arrestDocument.setSenderName(GenerateUtil.generateString(100));
        arrestDocument.setSenderCountry(findAny(CountryBook.class));
        arrestDocument.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        arrestDocument.setUnitType(findUnitType(arrestDocument.getCargoMode()));
        arrestDocument.setVehicleDetails(GenerateUtil.generateString(255));
        arrestDocument.setVehicleType(VEHICLE_TYPE);
        return arrestDocument;
    }
}
