/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vetcontrol.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class Statistics {

    private final List<Statistica> statistics;

    private final int clientCount;

    public Statistics(int clientCount) {
        this.clientCount = clientCount;
        statistics = new ArrayList<Statistica>(clientCount);
    }

    public void addStatistica(Statistica statistica) {
        synchronized (statistics) {
            statistics.add(statistica);
        }
    }

    public List<Statistica> getStatistics() {
        return statistics;
    }

    public float getMaxRequestTime() {
        float maxRequestTime = Float.MIN_VALUE;
        for (Statistica statistica : statistics) {
            float requestTime = statistica.getRequestTime();
            if (requestTime > maxRequestTime) {
                maxRequestTime = requestTime;
            }
        }
        return maxRequestTime;
    }

    public Map<Long, Float> getClientSyncTime() {
        Map<Long, Float> clientSyncTimeMap = new HashMap<Long, Float>(clientCount);
        for (Statistica statistica : statistics) {
            Long clientId = statistica.getClient().getId();
            Float clientSyncTime = clientSyncTimeMap.get(clientId);
            if (clientSyncTime == null) {
                clientSyncTime = statistica.getRequestTime();
            } else {
                clientSyncTime = clientSyncTime + statistica.getRequestTime();
            }
            clientSyncTimeMap.put(clientId, clientSyncTime);
        }
        return clientSyncTimeMap;
    }
}
