/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.db.populate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.vetcontrol.db.populate.util.GenerateUtil;
import org.vetcontrol.entity.Cargo;
import org.vetcontrol.entity.CargoProducer;
import org.vetcontrol.entity.CargoReceiver;
import org.vetcontrol.entity.CargoSender;
import org.vetcontrol.entity.CargoType;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.entity.Synchronized.SyncStatus;
import org.vetcontrol.entity.UnitType;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.service.CargoTypeBean;
import org.vetcontrol.service.ClientBean;
import org.vetcontrol.util.book.service.HibernateSessionTransformer;

/**
 *
 * @author Artem
 */
public class ClientPopulator extends AbstractPopulator {

    /*--------------------------Settings------------------------------------------------------*/
    private static final String CLIENT_PERSISTENCE_UNIT_NAME = "populate.client";
    //count of documents to generate
    private static final int DOCUMENT_COUNT = 10;
    //count of cargos onto one DocumentCargo entry.
    private static final int CARGO_COUNT = 1;
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
    }

    private void populateDocumentCargo() {
        if (department == null) {
            department = getEntityManager().getReference(Department.class, DEPARTMENT_ID);
        }
        if (creator == null) {
            creator = getEntityManager().getReference(User.class, CREATOR_ID);
        }
        if (client == null) {
            client = clientBean.getCurrentClient();
        }

        DocumentCargo dc = new DocumentCargo();
        dc.setDepartment(department);
        dc.setCreator(creator);
        dc.setClient(client);

        dc.setCargoProducer(findAny(CargoProducer.class));
        dc.setCargoReceiver(findAny(CargoReceiver.class));
        dc.setCargoSender(findAny(CargoSender.class));
        dc.setCreated(new Date());
        dc.setMovementType(findAny(MovementType.class));
        dc.setPassingBorderPoint(findAny(PassingBorderPoint.class));
        dc.setVehicleType(findAny(VehicleType.class));
        dc.setDetails(GenerateUtil.generateString(255));
        dc.setDetentionDetails(GenerateUtil.generateString(255));
        dc.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        dc.setVehicleDetails(GenerateUtil.generateString(255));

        HibernateSessionTransformer.getSession(getEntityManager()).saveOrUpdate(dc);

        System.out.println("Dc saved, id = " + dc.getId());

        for (int i = 0; i < CARGO_COUNT; i++) {
            Cargo cargo = new Cargo();
            cargo.setCertificateDate(new Date());
            cargo.setCertificateDetails(GenerateUtil.generateString(255));
            cargo.setClient(client);
            cargo.setCount(GenerateUtil.generateInt(1000));
            cargo.setDepartment(department);
            cargo.setDocumentCargoId(dc.getId());
            cargo.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);

            cargo.setCargoType(findAny(CargoType.class));
            cargo.setUnitType(findAny(UnitType.class));

            getEntityManager().persist(cargo);
        }
    }

    @Override
    protected void populate() {
        //document cargos
        for (int i = 0; i < DOCUMENT_COUNT; i++) {
            startTransaction();
            initClient();
            populateDocumentCargo();
            endTransaction();
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
