/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import com.sun.jersey.api.client.UniformInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import org.slf4j.Logger;
import org.vetcontrol.entity.Client;
import org.vetcontrol.sync.SyncRequestEntity;

/**
 *
 * @author Artem
 */
public abstract class Worker<T> implements Runnable {

    private Long startId;

    private Long endId;

    private Client client;

    private Logger log;

    private int networkBatchCount;

    private String serverPath;

    private Statistics statistics;

    private CyclicBarrier barrier;

    public Worker(Client client, Long startId, Long endId, Logger log, int networkBatchCount, String serverPath,
            Statistics statistics, CyclicBarrier barrier) {
        this.startId = startId;
        this.endId = endId;
        this.client = client;
        this.log = log;
        this.networkBatchCount = networkBatchCount;
        this.serverPath = serverPath;
        this.statistics = statistics;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        log.info("Work started. Client id : {}", client.getId());
        try {
            Id id = new Id(startId, endId);
            List<T> documents = new ArrayList<T>(networkBatchCount);

            while (id.canIncrement()) {
                T document = newDocument(id.getAndIncrement());
                documents.add(document);
                if (documents.size() % networkBatchCount == 0) {
                    postDocuments(documents);
                    Thread.sleep(1000L);
                }
            }
            if (!documents.isEmpty()) {
                postDocuments(documents);
            }
        } catch (Exception e) {
            log.error("Fatal unexpected problem. Client id : " + client.getId(), e);
        }
        log.info("Work finished. Client id : {}", client.getId());
        try {
            barrier.await();
        } catch (Exception e) {
            log.error("Barrier error. Client id : " + client.getId(), e);
        }
    }

    protected void postDocuments(List<T> documents) {
        UniformInterface uniformInterface = ClientFactory.createJSONClient(serverPath);
        long startTime = System.currentTimeMillis();
        try {
            uniformInterface.put(newRequestEntity(documents));
            float requestTime = getTimeDifference(startTime) / 1000.0F;
            statistics.addStatistica(new Statistica(requestTime, client));
            log.info("Successful posting of document cargos took {} sec. Client id : {}", requestTime, client.getId());
        } catch (Exception e) {
            float requestTime = getTimeDifference(startTime) / 1000.0F;
            statistics.addStatistica(new Statistica(requestTime, client, e));
            log.error("Unsuccessful posting of document cargos took " + requestTime
                    + " sec. Could not to post document cargo to server. Client id : " + client.getId(), e);
        }
        documents.clear();
    }

    protected Client getClient() {
        return client;
    }

    protected abstract SyncRequestEntity newRequestEntity(List<T> documents);

    protected abstract T newDocument(Long documentId);

    private static long getTimeDifference(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
