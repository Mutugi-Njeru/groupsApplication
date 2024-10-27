package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.model.Member;
import com.jacpower.groupsApp.model.MemberDto;
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
@RequestMapping("/member")
public class MemberController {
    private final Engine engine;

    @Autowired
    public MemberController(Engine engine) {
        this.engine = engine;
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addMember(@RequestBody @Valid Member member){
        JsonObject payload= Json.createObjectBuilder(member.toJsonObject())
                .add(Constants.REQUEST_TYPE, RequestTypes.CREATE_MEMBER.name())
                .build();
        return engine.routeRequest(payload, Modules.MEMBER.name());
    }

    @GetMapping(path = "/get/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getGroupMembers(@PathVariable ("groupId") int groupId){
        JsonObject payload=Json.createObjectBuilder()
                .add("groupId", groupId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_GROUP_MEMBERS.name())
                .build();
        return engine.routeRequest(payload, Modules.MEMBER.name());
    }
    @GetMapping(path = "/get/member/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getMemberById(@PathVariable ("memberId") int memberId){
        JsonObject payload=Json.createObjectBuilder()
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_MEMBER_BY_ID.name())
                .build();
        return engine.routeRequest(payload, Modules.MEMBER.name());
    }
    @PutMapping(value = "/update/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateMemberDetails(@PathVariable ("memberId") int memberId, @RequestBody @Valid MemberDto memberDto){
        JsonObject payload=Json.createObjectBuilder(memberDto.toJsonObject())
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.UPDATE_MEMBER_DETAILS.name())
                .build();
        return engine.routeRequest(payload, Modules.MEMBER.name());
    }
    @PutMapping(value = "/deactivate/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deactivateMember(@PathVariable ("memberId") int memberId){
        JsonObject payload=Json.createObjectBuilder()
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.DEACTIVATE_MEMBER.name())
                .build();
        return engine.routeRequest(payload, Modules.MEMBER.name());
    }
    @PutMapping(value = "/activate/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> activateMember(@PathVariable ("memberId") int memberId){
        JsonObject payload=Json.createObjectBuilder()
                .add("memberId", memberId)
                .add(Constants.REQUEST_TYPE, RequestTypes.ACTIVATE_MEMBER.name())
                .build();
        return engine.routeRequest(payload, Modules.MEMBER.name());
    }



}
