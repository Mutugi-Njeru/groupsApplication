package com.jacpower.groupsApp.ruleEngine.rules.user;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.AdminService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class AdminImplRule implements ServiceRule {
    private final AdminService adminService;
    @Autowired
    public AdminImplRule(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.ADMIN.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case CREATE_ADMIN:
                return Util.buildResponse(adminService.createAdmin(MyUser.fromJsonObject(requestBody)));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }

}
