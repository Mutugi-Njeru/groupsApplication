package com.jacpower.groupsApp.controller;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.model.UserDto;
import com.jacpower.groupsApp.ruleEngine.engine.Engine;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
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
                .add(Constants.REQUEST_TYPE, RequestTypes.CREATE_USER.name())
                .build();
        return engine.routeRequest(payload, Modules.USER.name());
    }
    @GetMapping(value = "/get/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserId (@PathVariable("username") String username){
        JsonObject payload=Json.createObjectBuilder()
                .add("username", username)
                .add(Constants.REQUEST_TYPE, RequestTypes.GET_USER_ID.name())
                .build();
        return engine.routeRequest(payload, Modules.USER.name());
    }

    @PutMapping(value = "/update/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUsernameAndPassword (@RequestBody @Valid UserDto userDto){
        JsonObject payload=Json.createObjectBuilder(userDto.userDtoToJson())
                .add(Constants.REQUEST_TYPE, RequestTypes.UPDATE_USERNAME_PASSWORD.name())
                .build();
        return engine.routeRequest(payload, Modules.USER.name());
    }

}
