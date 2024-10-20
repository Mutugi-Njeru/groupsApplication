package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final Engine engine;

    @Autowired
    public UserController(Engine engine) {
        this.engine = engine;
    }
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody @Valid MyUser user){
        JsonObject payload= Json.createObjectBuilder(user.userToJson())
                .add("requestType", RequestTypes.CREATE_USER.name())
                .build();
        return engine.routeRequest(payload, Modules.USER.name());
    }

}
