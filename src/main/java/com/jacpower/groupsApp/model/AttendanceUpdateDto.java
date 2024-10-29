package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceUpdateDto(
        @NotNull(message = "amount cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int amount,
        @NotNull(message = "attendanceId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int attendanceId,
        @NotBlank(message = "presence cannot be empty")
        String presence,
        @NotNull(message = "groupId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int groupId,
        @NotNull(message = "memberId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int memberId)
{
    public static AttendanceUpdateDto fromJsonObject(JsonObject object){
        return new AttendanceUpdateDto(
                object.getInt("amount"),
                object.getInt("attendanceId"),
                object.getString("presence"),
                object.getInt("groupId"),
                object.getInt("memberId")
        );
    }
    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("amount", amount)
                .add("attendanceId", attendanceId)
                .add("presence", presence)
                .add("groupId", groupId)
                .add("memberId", memberId)
                .build();
    }
}
