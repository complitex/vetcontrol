/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.db.populate;

import org.vetcontrol.db.populate.util.GenerateUtil;
import org.vetcontrol.entity.*;
import org.vetcontrol.entity.Synchronized.SyncStatus;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.ClientBean;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Artem
 */
public class ClientPopulator extends AbstractPopulator {

    /*--------------------------Settings------------------------------------------------------*/
    private static final String CLIENT_PERSISTENCE_UNIT_NAME = "populate.client";
    //count of documents to generate
    private static final int DOCUMENT_COUNT = 100;
    //count of cargos onto one DocumentCargo entry.
    private static final int CARGO_COUNT = 10;
    //Department id of creator of documents.
    private static final Long DEPARTMENT_ID = 17L;
    //User id of creator of documents.
    private static final Long CREATOR_ID = 30L;
    /*--------------------------- End settings -----------------------------------------------*/
    private ClientBean clientBean;
    private CargoTypeBean cargoTypeBean;
    private Department department;
    private User creator;
    private Client client;

    private void initClient() {
        clientBean = new ClientBean();
        clientBean.setEntityManager(getEntityManager());
        cargoTypeBean = new CargoTypeBean();
        cargoTypeBean.setEntityManager(getEntityManager());
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

    private void populateDocumentCargo(Date created) {
        DocumentCargo dc = new DocumentCargo();
        dc.setDepartment(department);
        dc.setCreator(creator);
        dc.setClient(client);

        dc.setCreated(created);
        dc.setUpdated(created);
        dc.setMovementType(findAny(MovementType.class));
        dc.setPassingBorderPoint(findAny(PassingBorderPoint.class));
        dc.setVehicleType(findAny(VehicleType.class));
        dc.setDetails(GenerateUtil.generateString(255));
        dc.setDetentionDetails(GenerateUtil.generateString(255));
        dc.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        dc.setVehicleDetails(GenerateUtil.generateString(255));

        getEntityManager().persist(dc);
    }

    private void populateCargo(Long documentCargoId) {
        Cargo cargo = new Cargo();
        cargo.setCertificateDate(new Date());
        cargo.setCertificateDetails(GenerateUtil.generateString(255));
        cargo.setClient(client);
        cargo.setCount(GenerateUtil.generateInt(1000));
        cargo.setDepartment(department);
        cargo.setDocumentCargoId(documentCargoId);
        cargo.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);

        String size = "SELECT COUNT(DISTINCT ct) FROM CargoModeCargoType cmct JOIN cmct.cargoType ct WHERE "
                + "EXISTS(SELECT 1 FROM CargoModeUnitType cmut WHERE cmut.id.cargoModeId = cmct.id.cargoModeId)";
        int count = getEntityManager().createQuery(size, Long.class).getSingleResult().intValue();
        if (count == 0) {
            throw new RuntimeException("There are no pertinent cargo types and unit types.");
        }
        String query = "SELECT ct FROM CargoModeCargoType cmct JOIN cmct.cargoType ct WHERE "
                + "EXISTS(SELECT 1 FROM CargoModeUnitType cmut WHERE cmut.id.cargoModeId = cmct.id.cargoModeId)";
        CargoType ct = getEntityManager().createQuery(query, CargoType.class).
                setFirstResult(GenerateUtil.generateInt(count - 1)).setMaxResults(1).getResultList().get(0);
        UnitType ut = cargoTypeBean.getUnitTypes(ct).get(0);

        cargo.setCargoType(ct);
        cargo.setUnitType(ut);
        getEntityManager().persist(cargo);
    }

    @Override
    protected void populate() {
        //document cargos
        Date now = new Date();
        for (int i = 0; i < DOCUMENT_COUNT; i++) {
            startTransaction();
            initClient();
            populateDocumentCargo(now);
            endTransaction();
        }
        List<Long> documentCargoIds = getEntityManager().createQuery("SELECT dc.id FROM DocumentCargo dc WHERE dc.created = :created", Long.class).
                setParameter("created", now).
                getResultList();
        for (Long documentCargoId : documentCargoIds) {
            for (int i = 0; i < CARGO_COUNT; i++) {
                startTransaction();
                initClient();
                populateCargo(documentCargoId);
                endTransaction();
            }
        }
    }

    @Override
    protected String getPersistenceUnitName() {
        return CLIENT_PERSISTENCE_UNIT_NAME;
    }

    public static void main(String[] args) {
        new ClientPopulator().populate();
    }
}
