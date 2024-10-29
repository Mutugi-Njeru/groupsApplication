package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Account;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/account")
public class AccountController {
    private final Engine engine;

    @Autowired
    public AccountController(Engine engine) {
        this.engine = engine;
    }

    @GetMapping(path = "/check/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> doesAccountExist(@PathVariable ("groupId") int groupId){
        JsonObject payload= Json.createObjectBuilder()
                .add("groupId", groupId)
                .add(Constants.REQUEST_TYPE, RequestTypes.DOES_ACCOUNT_EXIST.name())
                .build();
        return engine.routeRequest(payload, Modules.ACCOUNT.name());
    }
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account){
        JsonObject payload= Json.createObjectBuilder(account.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.CREATE_ACCOUNT.name())
                .build();
        return engine.routeRequest(payload, Modules.ACCOUNT.name());
    }
    @GetMapping(path = "/get/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAccountDetails(@PathVariable ("groupId") int groupId){
        JsonObject payload=Json.createObjectBuilder()
                .add("groupId", groupId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_ACCOUNT_DETAILS.name())
                .build();
        return engine.routeRequest(payload, Modules.ACCOUNT.name());
    }
}
