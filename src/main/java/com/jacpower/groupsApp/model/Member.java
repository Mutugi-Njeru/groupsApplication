package com.jacpower.groupsApp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Member(
        @NotNull(message = "userId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int groupId,
        String firstname,
        String lastname,
        String phoneNumber,
        String idNumber,
        String email,
        String username,
        String password)
{

}
