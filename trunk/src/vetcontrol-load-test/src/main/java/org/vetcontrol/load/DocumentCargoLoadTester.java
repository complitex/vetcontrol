/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import com.sun.jersey.api.client.UniformInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.db.populate.util.GenerateUtil;
import org.vetcontrol.entity.CargoMode;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.CountryBook;
import org.vetcontrol.entity.Department;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.entity.PassingBorderPoint;
import org.vetcontrol.entity.Synchronized.SyncStatus;
import org.vetcontrol.entity.User;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.hibernate.util.HibernateSessionTransformer;
import org.vetcontrol.sync.SyncDocumentCargo;

/**
 *
 * @author Artem
 */
public class DocumentCargoLoadTester {

    private static final Logger log = LoggerFactory.getLogger(DocumentCargoLoadTester.class);

    private static final String PERSISTENCE_UNIT_NAME = "applicationPersistenceUnitTest";

    private static final String PATH = "/document/document_cargo";

    private static final Long START_ID = 2000L;

    private static final int CLIENT_COUNT = 10;

    private static final long DOCUMENTS_PER_CLIENT = 100L;

    private static final int NETWORK_BATCH = 100;

    private class Worker implements Runnable {

        private Long startId;

        private Long endId;

        private Client client;

        public Worker(Client client, Long startId, Long endId) {
            this.startId = startId;
            this.endId = endId;
            this.client = client;
        }

        @Override
        public void run() {
            log.info("Work started. Client id : {}", client.getId());
            try {
                UniformInterface uniformInterface = ClientFactory.createJSONClient(PATH);
                Id id = new Id(startId, endId);
                List<DocumentCargo> documents = new ArrayList<DocumentCargo>(NETWORK_BATCH);

                while (id.canIncrement()) {
                    DocumentCargo documentCargo = newDocumentCargo(id.getAndIncrement(), client);
                    documents.add(documentCargo);
                    if (documents.size() % NETWORK_BATCH == 0) {
                        postDocuments(uniformInterface, documents);
                    }
                }
                if (!documents.isEmpty()) {
                    postDocuments(uniformInterface, documents);
                }
            } catch (Exception e) {
                log.error("Fatal unexpected problem. Client id : " + client.getId(), e);
            }
            log.info("Work finished. Client id : {}", client.getId());
        }

        private void postDocuments(UniformInterface uniformInterface, List<DocumentCargo> documents) {
            long startTime = System.currentTimeMillis();
            try {
                uniformInterface.put(new SyncDocumentCargo(client.getSecureKey(), null, documents));
                log.info("Successful posting of document cargos took {} sec. Client id : {}", getTimeDifference(startTime) / 1000.0, client.getId());
                documents.clear();
            } catch (Exception e) {
                log.error("Unsuccessful posting of document cargos took " + getTimeDifference(startTime) / 1000.0
                        + " sec. Could not to post document cargo to server. Client id : " + client.getId(), e);
            }
        }
    }

    public Client[] initClients() {
        try {
            Client[] clients = new Client[CLIENT_COUNT];

            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            for (int i = 0; i < CLIENT_COUNT; i++) {
                Client c = new Client();
                c.setCreated(new Date());
                c.setUpdated(new Date());
                c.setDepartment(newDepartment());
                c.setIp("0.0.0.0");
                c.setMac(String.valueOf(i));
                c.setSecureKey(String.valueOf(i));
                HibernateSessionTransformer.getSession(entityManager).saveOrUpdate(c);
                clients[i] = c;
            }

            transaction.commit();
            entityManager.close();
            entityManagerFactory.close();

            return clients;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void test() {
        Client[] clients = initClients();

        long startId = START_ID;
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_COUNT);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            executorService.execute(new Worker(clients[i], startId, startId + DOCUMENTS_PER_CLIENT));
            startId = startId + DOCUMENTS_PER_CLIENT;
        }
        executorService.shutdownNow();
    }

    private DocumentCargo newDocumentCargo(Long id, Client client) {
        DocumentCargo documentCargo = new DocumentCargo();
        documentCargo.setId(id);
        documentCargo.setDepartment(newDepartment());
        documentCargo.setCreator(newCreator());
        documentCargo.setClient(client);
        documentCargo.setCreated(new Date());
        documentCargo.setUpdated(new Date());
        documentCargo.setMovementType(MovementType.IMPORT);
        documentCargo.setPassingBorderPoint(newPassingBorderPoint());
        documentCargo.setVehicleType(VehicleType.CAR);
        documentCargo.setDetails(GenerateUtil.generateString(255));
        documentCargo.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        documentCargo.setReceiverAddress(GenerateUtil.generateString(100));
        documentCargo.setReceiverName(GenerateUtil.generateString(100));
        documentCargo.setSenderName(GenerateUtil.generateString(100));
        documentCargo.setSenderCountry(newSenderCountry());
        documentCargo.setCargoMode(newCargoMode());
        return documentCargo;
    }

    private Department newDepartment() {
        Department department = new Department();
        department.setId(1L);
        return department;
    }

    private User newCreator() {
        User creator = new User();
        creator.setId(1L);
        return creator;
    }

    private CountryBook newSenderCountry() {
        CountryBook senderCountry = new CountryBook();
        senderCountry.setId(1L);
        return senderCountry;
    }

    private CargoMode newCargoMode() {
        CargoMode cargoMode = new CargoMode();
        cargoMode.setId(1L);
        return cargoMode;
    }

    private PassingBorderPoint newPassingBorderPoint() {
        PassingBorderPoint borderPoint = new PassingBorderPoint();
        borderPoint.setId(1L);
        return borderPoint;
    }

    private static long getTimeDifference(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    public static void main(String[] args) {
        DocumentCargoLoadTester tester = new DocumentCargoLoadTester();
        tester.test();
    }
}
