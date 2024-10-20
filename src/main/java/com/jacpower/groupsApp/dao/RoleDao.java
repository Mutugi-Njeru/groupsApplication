package com.jacpower.groupsApp.dao;

import com.jacpower.groupsApp.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class RoleDao {
    private final JdbcClient jdbcClient;
    private static final Logger logger= LoggerFactory.getLogger(RoleDao.class);
    @Autowired
    public RoleDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Set<Role> getRoles (String username){
        String query= """
                SELECT r.name FROM roles r
                INNER JOIN user_roles ur ON r.role_id=ur.role_id
                INNER JOIN users u ON u.user_id=ur.user_id
                WHERE u.username=? AND u.is_active=true""";
        try {
            return jdbcClient.sql(query)
                    .param(username)
                    .query(Role.class)
                    .set();
        }
        catch (Exception e){
            logger.error("error message=========>{}", e.getMessage());
            throw e;
        }

    }
    //add user role
    public int addUserRole(int userId, int roleId){
        String query="INSERT INTO user_roles (user_id, role_id) VALUES (?,?)";
        GeneratedKeyHolder generatedKeyHolder=new GeneratedKeyHolder();
        try {
            jdbcClient.sql(query)
                    .params(List.of(userId, roleId))
                    .update(generatedKeyHolder);
            return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        }
        catch (Exception e){
            logger.error("error message=======>{}", e.getMessage());
            throw  e;
        }
    }
}
