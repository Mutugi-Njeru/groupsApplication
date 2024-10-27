package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.GroupDao;
import com.jacpower.groupsApp.model.Group;
import com.jacpower.groupsApp.records.ServiceResponder;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupService {
    private final GroupDao groupDao;
    @Autowired
    public GroupService(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public ServiceResponder createGroup(Group group){
        boolean isExists= groupDao.isGroupExist(group.registrationPin(), group.groupName(), group.emailAddress());
        if (!isExists){
            int groupId= groupDao.createGroup(group);
            return (groupId>0)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "group created successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot create group");
        }
        else return  new ServiceResponder(HttpStatus.BAD_REQUEST, false, "group with similar details already exists");
    }
    public ServiceResponder getGroupDetails (int userId){
        Optional<JsonObject> groupDetails = groupDao.getGroupDetails(userId);
       // JsonObject group=groupDetails.orElseThrow(()-> new UsernameNotFoundException("group not found"));
        if (groupDetails.isPresent()){
            JsonObject group=groupDetails.get();
            return (!group.isEmpty())
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, group)
                    :new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createObjectBuilder().build());
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, Json.createObjectBuilder().build());
    }

    public ServiceResponder updateGroup(Group group){
        boolean isUpdated= groupDao.updateGroupDetails(group);
        return (isUpdated)
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, "group updated successfully")
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot update group");
    }
    public ServiceResponder getGroupId(int userId){
        int groupId= groupDao.getGroupId(userId);
        return new ServiceResponder(HttpStatus.ACCEPTED, true, Json.createObjectBuilder().add("groupId", groupId).build());
    }

}













