package com.jacpower.groupsApp.ruleEngine.rules.user;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.UserService;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class UserImplRule implements ServiceRule {
    private final UserService userService;
    @Autowired
    public UserImplRule(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.USER.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)){
            case CREATE_USER:
                return Util.buildResponse(userService.createUser(MyUser.fromJsonObject(requestBody)));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
