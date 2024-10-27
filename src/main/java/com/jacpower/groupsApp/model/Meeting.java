package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Meeting(
        @NotNull(message = "groupId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int groupId,
        @NotBlank(message = "meetingDate cannot be empty")
        String meetingDate,
        @NotBlank(message = "appearance cannot be empty")
        @Size(min = 2, message = "appearance must be more than 2 characters")
        String appearance,
        @NotBlank(message = "objective cannot be empty")
        @Size(min = 2, message = "objective must be more than 2 characters")
        String objective,
        @NotBlank(message = "location cannot be empty")
        @Size(min = 2, message = "location must be more than 2 characters")
        String location)
{

    public JsonObject toJsonObject(){
       return Json.createObjectBuilder()
                .add("groupId", groupId)
                .add("meetingDate", meetingDate)
                .add("appearance", appearance)
                .add("objective", objective)
                .add("location", location)
                .build();
    }
    public static Meeting fromJsonObject(JsonObject object){
        return  new Meeting(
                object.getInt("groupId"),
                object.getString("meetingDate"),
                object.getString("appearance"),
                object.getString("objective"),
                object.getString("location")
        );
    }
}
