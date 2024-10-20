package com.jacpower.groupsApp.ruleEngine.rules.role;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.RoleService;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class RoleImplRule implements ServiceRule {
    private final RoleService roleService;

    @Autowired
    public RoleImplRule(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.ROLE.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)) {
            case GET_ROLE:
                return Util.buildResponse(roleService.getRole(requestBody.getString("username")));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
