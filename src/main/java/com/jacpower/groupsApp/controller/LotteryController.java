package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Lottery;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/lottery")
public class LotteryController {
    private final Engine engine;

    @Autowired
    public LotteryController(Engine engine) {
        this.engine = engine;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addLottery(@RequestBody @Valid Lottery lottery){
        JsonObject payload= Json.createObjectBuilder(lottery.toJsonObject())
                .add("requestType", RequestTypes.ADD_UPDATE_LOTTERY.name())
                .build();
        return engine.routeRequest(payload, Modules.LOTTERY.name());
    }
    @GetMapping(value = "/get/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getWinner(@PathVariable ("groupId") int groupId){
        JsonObject payload= Json.createObjectBuilder()
                .add("groupId", groupId)
                .add("requestType", RequestTypes.GET_WINNER.name())
                .build();
        return engine.routeRequest(payload, Modules.LOTTERY.name());
    }
    @PostMapping(value = "/approve/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> approveLottery(@PathVariable ("memberId") int memberId){
        JsonObject payload= Json.createObjectBuilder()
                .add("memberId", memberId)
                .add("requestType", RequestTypes.APPROVE_LOTTERY.name())
                .build();
        return engine.routeRequest(payload, Modules.LOTTERY.name());
    }
    @GetMapping(value = "/summary/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLotterySummary(@PathVariable ("groupId") int groupId){
        JsonObject payload= Json.createObjectBuilder()
                .add("groupId", groupId)
                .add("requestType", RequestTypes.GET_LOTTERY_SUMMARY.name())
                .build();
        return engine.routeRequest(payload, Modules.LOTTERY.name());
    }
}
