/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vetcontrol.db.populate.util.GenerateUtil;
import org.vetcontrol.entity.Client;
import org.vetcontrol.entity.DocumentCargo;
import org.vetcontrol.entity.MovementType;
import org.vetcontrol.entity.Synchronized.SyncStatus;
import org.vetcontrol.entity.VehicleType;
import org.vetcontrol.sync.SyncDocumentCargo;
import org.vetcontrol.sync.SyncRequestEntity;

/**
 *
 * @author Artem
 */
public class DocumentCargoLoadTester {

    private static final Logger log = LoggerFactory.getLogger(DocumentCargoLoadTester.class);

    private static final String PATH = "/document/document_cargo";

    private static final Long START_ID = 2000L;

    private static final int CLIENT_COUNT = 300;

    private static final long DOCUMENTS_PER_CLIENT = 300L;

    private static final int NETWORK_BATCH = 100;

    public void test() {
        Client[] clients = Util.initClients(CLIENT_COUNT);

        long startId = START_ID;
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_COUNT);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            executorService.execute(new Worker<DocumentCargo>(clients[i], startId, startId + DOCUMENTS_PER_CLIENT, log, NETWORK_BATCH, PATH) {

                @Override
                protected SyncRequestEntity newRequestEntity(List<DocumentCargo> documents) {
                    return new SyncDocumentCargo(getClient().getSecureKey(), null, documents);
                }

                @Override
                protected DocumentCargo newDocument(Long documentId) {
                    return newDocumentCargo(documentId, getClient());
                }
            });
            startId = startId + DOCUMENTS_PER_CLIENT;
        }
        executorService.shutdownNow();
    }

    private DocumentCargo newDocumentCargo(Long id, Client client) {
        DocumentCargo documentCargo = new DocumentCargo();
        documentCargo.setId(id);
        documentCargo.setDepartment(Util.newDepartment());
        documentCargo.setCreator(Util.newCreator());
        documentCargo.setClient(client);
        documentCargo.setCreated(new Date());
        documentCargo.setUpdated(new Date());
        documentCargo.setMovementType(MovementType.IMPORT);
        documentCargo.setPassingBorderPoint(Util.newPassingBorderPoint());
        documentCargo.setVehicleType(VehicleType.CAR);
        documentCargo.setDetails(GenerateUtil.generateString(255));
        documentCargo.setSyncStatus(SyncStatus.NOT_SYNCHRONIZED);
        documentCargo.setReceiverAddress(GenerateUtil.generateString(100));
        documentCargo.setReceiverName(GenerateUtil.generateString(100));
        documentCargo.setSenderName(GenerateUtil.generateString(100));
        documentCargo.setSenderCountry(Util.newSenderCountry());
        documentCargo.setCargoMode(Util.newCargoMode());
        return documentCargo;
    }

    public static void main(String[] args) {
        DocumentCargoLoadTester tester = new DocumentCargoLoadTester();
        tester.test();
    }
}
