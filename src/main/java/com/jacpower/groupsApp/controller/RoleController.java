package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/role")
public class RoleController {
    private final Engine engine;

    public RoleController(Engine engine) {
        this.engine = engine;
    }
    @GetMapping("/get/{name}")
    public ResponseEntity<Object> getRole(@PathVariable("name")String name){
        JsonObject payload= Json.createObjectBuilder()
                .add("username", name)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_ROLE.name())
                .build();
        return engine.routeRequest(payload, Modules.ROLE.name());
    }
}
