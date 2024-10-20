package com.jacpower.groupsApp.records;

import org.springframework.http.HttpStatus;

public record ServiceResponder(HttpStatus status, boolean isSuccess, Object message) {
}
