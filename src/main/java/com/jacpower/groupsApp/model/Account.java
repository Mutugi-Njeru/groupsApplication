package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Account (
        @NotNull(message = "groupId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int groupId,

        @NotBlank(message = "accountName cannot be empty")
        @Size(min = 1, message = "accountName must have at least one digit")
        String accountName)
{
    public static Account fromJsonObject(JsonObject object){
        return new Account(
                object.getInt("groupId"),
                object.getString("accountName")
        );
    }

    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("groupId", groupId)
                .add("accountName", accountName)
                .build();
    }
}
