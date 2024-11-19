package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.model.Loan;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/loan")
public class LoanController {
    private final Engine engine;

    public LoanController(Engine engine) {
        this.engine = engine;
    }

    @PostMapping(path = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> requestLoan(@RequestBody @Valid Loan loan){
        JsonObject payload= Json.createObjectBuilder(loan.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.REQUEST_LOAN.name())
                .build();
        return engine.routeRequest(payload, Modules.LOAN.name());
    }

    @PutMapping(path = "/approve/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> approveLoan(@PathVariable ("memberId") int memberId){
        JsonObject payload= Json.createObjectBuilder()
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.APPROVE_LOAN.name())
                .build();
        return engine.routeRequest(payload, Modules.LOAN.name());
    }
    @PutMapping(path = "/deny/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> denyLoan(@PathVariable ("memberId") int memberId){
        JsonObject payload= Json.createObjectBuilder()
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.DENY_LOAN.name())
                .build();
        return engine.routeRequest(payload, Modules.LOAN.name());
    }
    @GetMapping(path = "/get/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLoans(@PathVariable ("groupId") int groupId){
        JsonObject payload= Json.createObjectBuilder()
                .add("groupId", groupId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_LOANS.name())
                .build();
        return engine.routeRequest(payload, Modules.LOAN.name());
    }
    @PutMapping(path = "/repay/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> repayLoan(@PathVariable ("memberId") int memberId){
        JsonObject payload= Json.createObjectBuilder()
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.REPAY_LOAN.name())
                .build();
        return engine.routeRequest(payload, Modules.LOAN.name());
    }
}
