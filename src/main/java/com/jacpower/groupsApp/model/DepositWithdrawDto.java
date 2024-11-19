package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepositWithdrawDto(
        @NotNull(message = "userId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int userId,
        @NotNull(message = "amount cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int amount,
        @NotBlank(message = "description cannot be empty")
        @Size(min = 4, message = "description must have more than three digits")
        String description)
{
    public static DepositWithdrawDto fromJsonObject(JsonObject object){
        return new DepositWithdrawDto(
                object.getInt("userId"),
                object.getInt("amount"),
                object.getString("description")
        );
    }
    public JsonObject toJsonObject (){
        return Json.createObjectBuilder()
                .add("userId", userId)
                .add("amount", amount)
                .add("description", description)
                .build();
    }
}
