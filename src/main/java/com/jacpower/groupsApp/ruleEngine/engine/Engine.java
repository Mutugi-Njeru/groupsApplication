package com.jacpower.groupsApp.ruleEngine.engine;

import com.jacpower.groupsApp.ruleEngine.interfaces.ServiceRule;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Engine {
    private static final Logger logger = LoggerFactory.getLogger(Engine.class);

    private final List<ServiceRule> rules;

    @Autowired
    public Engine(List<ServiceRule> rules)
    {
        this.rules = rules;
    }

    public ResponseEntity<Object> routeRequest(JsonObject request, String module)
    {

        logger.info("Module received==========>{}", module);
        for (ServiceRule rule : rules)
        {
            if (rule.matches(module))
            {
                return ResponseEntity.ok(rule.apply(request).toString());
            }
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Unknown request module. please try again");

    }
}
