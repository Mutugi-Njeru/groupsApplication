package com.jacpower.groupsApp.ruleEngine.rules.group;

import com.jacpower.groupsApp.enums.Modules;
import com.jacpower.groupsApp.enums.RequestTypes;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import com.jacpower.groupsApp.ruleEngine.service.GroupService;
import com.jacpower.groupsApp.utility.Constants;
import com.jacpower.groupsApp.utility.Util;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class GroupImplRule implements ServiceRule {
    private final GroupService groupService;
    @Autowired
    public GroupImplRule(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.GROUP.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Json.createReader(new StringReader(request.toString())).readObject();
        String requestType = requestBody.getString(Constants.REQUEST_TYPE, "");

        switch (RequestTypes.valueOf(requestType)){
            case CREATE_GROUP:
                return Util.buildResponse(groupService.createGroup(Group.fromJsonObject(requestBody)));
            case GET_GROUP_DETAILS:
                return Util.buildResponse(groupService.getGroupDetails(requestBody.getInt("userId")));
            case UPDATE_GROUP:
                return Util.buildResponse(groupService.updateGroup(Group.fromJsonObject(requestBody)));
            default:
                throw new IllegalArgumentException("Unexpected request type: " + requestType);
        }
    }
}
