package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.RoleDao;
import com.jacpower.groupsApp.dao.UserDao;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.records.ServiceResponder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    //create admin
    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public AdminService(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }
    public ServiceResponder createAdmin (MyUser user){
        //check if exists
        //add user
        //add user details
        // add user role
        boolean isExists= userDao.isUserExist(user.email(), user.username());
        if (!isExists){
            int userId= userDao.createUser(user);
            int detailsId=userDao.addUserDetails(userId, user);
            int roleId= roleDao.addUserRole(userId, 1); //admin roles
            return (userId>0 && detailsId>0 && roleId>0)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "admin created successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot create admin");
        }
        else return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "email or username already taken" );
    }
}
