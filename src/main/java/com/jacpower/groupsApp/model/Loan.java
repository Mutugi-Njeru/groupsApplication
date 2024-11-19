package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Loan(
        @NotNull(message = "memberId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int memberId,
        @NotNull(message = "amount cannot be empty")
        @Min(value = 500, message = "minimum you can borrow is 500")
        int amount)
{
    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("memberId", memberId)
                .add("amount", amount)
                .build();
    }
    public static Loan fromJsonObject(JsonObject object){
        return new Loan(
                object.getInt("memberId"),
                object.getInt("amount")
        );
    }
}
