/*This DTO will handle the information required when a new user registers,
such as their username, email, password, and potentially a password confirmation.
* */

package com.example.taskmanager.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(min = 5, message = "Email must be at least 5 characters long")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Userngtame must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter and one number")
    private String password;

    @NotBlank(message = "Role is required")
    private String roleName;
}
