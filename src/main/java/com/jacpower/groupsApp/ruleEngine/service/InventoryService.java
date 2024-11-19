package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.InventoryDao;
import com.jacpower.groupsApp.records.ServiceResponder;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryDao inventoryDao;

    @Autowired
    public InventoryService(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    public ServiceResponder getInventoryDetails(int groupId) {
        List<JsonObject> inventoryDetails = inventoryDao.getInventoryDetails(groupId);
        JsonArray inventory = Util.convertListToJsonArray(inventoryDetails);
        return (!inventory.isEmpty())
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, inventory)
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createArrayBuilder().build());
    }
}
