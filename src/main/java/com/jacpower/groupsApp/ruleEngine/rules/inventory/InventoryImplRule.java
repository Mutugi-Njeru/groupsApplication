package com.jacpower.groupsApp.ruleEngine.rules.inventory;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.InventoryService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class InventoryImplRule implements ServiceRule {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryImplRule(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.INVENTORY.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case GET_INVENTORY_DETAILS:
                return Util.buildResponse(inventoryService.getInventoryDetails(requestBody.getInt("groupId")));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
