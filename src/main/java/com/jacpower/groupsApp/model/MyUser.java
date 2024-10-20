package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.*;

public record MyUser (
    @NotBlank(message = "firstname cannot be empty")
    @Size(min = 2, max = 50, message = "firstname must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "firstname must contain only letters")
     String firstname,

    @NotBlank(message = "firstname cannot be empty")
    @Size(min = 2, max = 50, message = "firstname must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "lastname must contain only letters")
     String lastname,

    @NotBlank(message = "msisdn cannot be empty")
    @Size(min = 12, message = "msisdn must have 12 characters")
    @Pattern(regexp = "^[0-9]*$", message = "msisdn must contain only numbers")
     String phoneNumber,

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

        public static MyUser fromJsonObject (JsonObject object){
            return new MyUser(
                    object.getString("firstname"),
                    object.getString("lastname"),
                    object.getString("phoneNumber"),
                    object.getString("email"),
                    object.getString("username"),
                    object.getString("password")
            );
        }
        public JsonObject userToJson(){
            return Json.createObjectBuilder()
                    .add("firstname", firstname)
                    .add("lastname", lastname)
                    .add("phoneNumber", phoneNumber)
                    .add("email", email)
                    .add("username", username)
                    .add("password", password)
                    .build();
        }
}
