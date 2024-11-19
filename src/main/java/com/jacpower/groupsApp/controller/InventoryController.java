package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final Engine engine;

    @Autowired
    public InventoryController(Engine engine) {
        this.engine = engine;
    }
    @GetMapping(path = "/get/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getInventoryDetails(@PathVariable("groupId") int groupId) {
        JsonObject payload = Json.createObjectBuilder()
                .add("groupId", groupId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_INVENTORY_DETAILS.name())
                .build();
        return engine.routeRequest(payload, Modules.INVENTORY.name());
    }
}
