package com.jacpower.groupsApp.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDao {
    private final JdbcClient jdbcClient;
    private final Logger logger= LoggerFactory.getLogger(GroupDao.class);
    @Autowired
    public GroupDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //check if group exists by registration pin and group name
    //create group

}
