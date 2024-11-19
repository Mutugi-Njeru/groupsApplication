package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Lottery(
        @NotNull(message = "groupId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int groupId,
        @NotNull(message = "amount cannot be empty")
        int amount) {
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add("groupId", groupId)
                .add("amount", amount)
                .build();
    }

    public static Lottery fromJsonObject(JsonObject object) {
        return new Lottery(
                object.getInt("groupId"),
                object.getInt("amount")
        );

    }
}
