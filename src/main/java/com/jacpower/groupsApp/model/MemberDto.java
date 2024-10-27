package com.jacpower.groupsApp.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.*;

public record MemberDto(
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
        String phone,
        @NotBlank(message = "idNumber cannot be blank")
        @Size(min = 5, message = "idNumber should have at least 5 numbers")
        String idNumber,
        @NotBlank(message = "Email is required")
        @Email(message = "please put a valid email")
        String email)
{
    public static MemberDto fromJsonObject(JsonObject object){
        return new MemberDto(
                object.getString("firstname"),
                object.getString("lastname"),
                object.getString("phone"),
                object.getString("idNumber"),
                object.getString("email")
        );
    }
    public JsonObject toJsonObject(){
        return Json.createObjectBuilder()
                .add("firstname", firstname)
                .add("lastname", lastname)
                .add("phone", phone)
                .add("idNumber", idNumber)
                .add("email", email)
                .build();
    }
}
