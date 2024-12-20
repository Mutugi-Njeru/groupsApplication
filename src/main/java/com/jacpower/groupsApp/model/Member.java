package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.*;

public record Member(
        @NotNull(message = "groupId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int groupId,
        @NotBlank(message = "firstname cannot be empty")
        @Size(min = 2, max = 50, message = "firstname must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "firstname must contain only letters")
        String firstname,
        @NotBlank(message = "firstname cannot be empty")
        @Size(min = 2, max = 50, message = "firstname must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "lastname must contain only letters")
        String lastname,
        @NotBlank(message = "msisdn cannot be empty")
        @Size(min = 12, message = "phoneNumber must have 12 characters")
        @Pattern(regexp = "^[0-9]*$", message = "phoneNumber must contain only numbers")
        String phoneNumber,
        @NotBlank(message = "idNumber cannot be blank")
        @Size(min = 5, message = "idNumber should have at least 5 numbers")
        String idNumber,
        @NotBlank(message = "Email is required")
        @Email(message = "please put a valid email")
        String email,
        @NotBlank(message = "username cannot be empty")
        @Size(min = 2, max = 50, message = "username must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "username must contain only letters and numbers")
        String username,
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:\"<>?`~\\-\\[\\]\\\\;'.,/]).{8,}$",
                message = "password must contain at least one letter, one number, and one special character and more than 8 characters")
        String password)
{
    public static Member fromJsonObject(JsonObject object){
        return new Member(
                object.getInt("groupId"),
                object.getString("firstname"),
                object.getString("lastname"),
                object.getString("phoneNumber"),
                object.getString("idNumber"),
                object.getString("email"),
                object.getString("username"),
                object.getString("password")
        );
    }
    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("groupId", groupId)
                .add("firstname", firstname)
                .add("lastname", lastname)
                .add("phoneNumber", phoneNumber)
                .add("idNumber", idNumber)
                .add("email", email)
                .add("username", username)
                .add("password", password)
                .build();
    }

}
