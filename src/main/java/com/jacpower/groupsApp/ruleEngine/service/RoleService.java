package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.RoleDao;
import com.jacpower.groupsApp.records.ServiceResponder;
import jakarta.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleDao roleDao;
    @Autowired
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public ServiceResponder getRole(String username){
        String role= roleDao.getRole(username);
        return new ServiceResponder(HttpStatus.ACCEPTED, true, Json.createObjectBuilder().add("role", role).build());
    }
}
