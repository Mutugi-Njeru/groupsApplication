package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Attendance (
        @NotNull(message = "meetingId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int meetingId,
        @NotNull(message = "memberId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int memberId,
        @NotBlank(message = "presence cannot be empty")
        @Size(min = 4, message = "presence must have more than three digits")
        String presence)
{
    public static Attendance fromJsonObject(JsonObject object){
        return new Attendance(
                object.getInt("meetingId"),
                object.getInt("memberId"),
                object.getString("presence")
        );
    }
    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("meetingId", meetingId)
                .add("memberId", memberId)
                .add("presence", presence)
                .build();
    }



}
