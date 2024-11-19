package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.*;

public record UserDto(
        @NotNull(message = "userId cannot be empty")
        @Min(value = 1, message = "The value must be at least 1")
        int userId,
        @NotBlank(message = "username cannot be empty")
        @Size(min = 2, max = 50, message = "username must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "username must contain only letters and numbers")
        String username,

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:\"<>?`~\\-\\[\\]\\\\;'.,/]).{8,}$",
         message = "password must contain at least one letter, one number, and one special character and more than 8 characters")
        String password)
{

    public static UserDto fromJsonObject (JsonObject object){
        return new UserDto(

                object.getInt("userId"),
                object.getString("username"),
                object.getString("password")
        );
    }
    public JsonObject userDtoToJson(){
        return Json.createObjectBuilder()
                .add("userId", userId)
                .add("username", username)
                .add("password", password)
                .build();
    }
}
