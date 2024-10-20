package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.*;

public record Group(
        @NotNull(message = "userId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int userId,
        @NotBlank(message = "groupName cannot be empty")
        @Size(min = 2, message = "groupName must have more than one letter")
        @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "groupName can only contain letters and spaces")
        String groupName,
        @Email(message = "please put a valid email")
        @NotBlank(message = "email cannot be blank")
        String emailAddress,
        @NotBlank(message = "registrationPin cannot be empty")
        @Size(min = 4, message = "registrationPin must have more than three digits")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "registrationPin can only contain letters and numbers")
        String registrationPin,
        @NotBlank(message = "address cannot be empty")
        @Size(min = 4, message = "address must have more than three digits")
        String address,
        @NotBlank(message = "phoneNumber cannot be empty")
        @Size(min = 12, message = "phoneNumber must have 12 digits")
        String phoneNumber,
        @NotBlank(message = "group description cannot be empty")
        String groupDescription)
{
    public static Group fromJsonObject (JsonObject object){
        return new Group(
                object.getInt("userId"),
                object.getString("groupName"),
                object.getString("emailAddress"),
                object.getString("registrationPin"),
                object.getString("address"),
                object.getString("phoneNumber"),
                object.getString("groupDescription")
        );
    }
    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("userId", userId)
                .add("groupName", groupName)
                .add("emailAddress", emailAddress)
                .add("registrationPin", registrationPin)
                .add("address", address)
                .add("phoneNumber", phoneNumber)
                .add("groupDescription", groupDescription)
                .build();
    }

}
