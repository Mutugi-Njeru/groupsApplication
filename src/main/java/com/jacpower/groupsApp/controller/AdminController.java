package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final Engine engine;

    @Autowired
    public AdminController(Engine engine) {
        this.engine = engine;
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAdmin(@RequestBody @Valid MyUser user){
        JsonObject payload= Json.createObjectBuilder(user.userToJson())
                .add(Constants.REQUEST_TYPE, RequestTypes.CREATE_ADMIN.name())
                .build();
        return engine.routeRequest(payload, Modules.ADMIN.name());
    }
}
