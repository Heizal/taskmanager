/*This dto needs the username and password only
* */
package com.example.taskmanager.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotBlank(message = "Username or email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}
