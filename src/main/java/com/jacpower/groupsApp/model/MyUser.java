package com.jacpower.groupsApp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyUser {
    @NotBlank(message = "firstname cannot be empty")
    @Size(min = 2, max = 50, message = "firstname must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "firstname must contain only letters and spaces")
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
    private String username;
    private String password;

}
