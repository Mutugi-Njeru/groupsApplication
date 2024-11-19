package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.dao.MemberDao;
import com.jacpower.groupsApp.dao.RoleDao;
import com.jacpower.groupsApp.dao.UserDao;
import com.jacpower.groupsApp.model.MyUser;
import com.jacpower.groupsApp.model.UserDto;
import com.jacpower.groupsApp.records.ServiceResponder;
import jakarta.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final MemberDao memberDao;
    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao, MemberDao memberDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.memberDao = memberDao;
    }

    //create  user
    public ServiceResponder createUser (MyUser user){
        //check if exists
        //add user
        //add user details
        // add user role
        boolean isExists= userDao.isUserExist(user.email(), user.username());
        if (!isExists){
            int userId= userDao.createUser(user);
            int detailsId=userDao.addUserDetails(userId, user);
            int roleId= roleDao.addUserRole(userId, 2); //user roles
            return (userId>0 && detailsId>0 && roleId>0)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "user created successfully")
                    : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot create user");
        }
        else return new ServiceResponder(HttpStatus.BAD_REQUEST, false, "email or username already taken" );
    }

    public ServiceResponder getUserId(String username){
        int userId= userDao.getUserId(username);
        return (userId>0)
                ? new ServiceResponder(HttpStatus.ACCEPTED, true, Json.createObjectBuilder().add("userId", userId).build())
                : new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "cannot get userId");
    }
    public ServiceResponder updateUsernameAndPassword(UserDto userDto){
        boolean isNameTaken= memberDao.isUsernameTaken(userDto.username());
        if (!isNameTaken){
            boolean isUpdated= userDao.updateUsernameAndPassword(userDto.userId(), userDto.username(), userDto.password());
            return (isUpdated)
                    ? new ServiceResponder(HttpStatus.ACCEPTED, true, "details updated successfully")
                    : new ServiceResponder(HttpStatus.FORBIDDEN, false, "cannot update details");
        }
        else return new ServiceResponder(HttpStatus.EXPECTATION_FAILED, false, "username already taken");
    }
}
