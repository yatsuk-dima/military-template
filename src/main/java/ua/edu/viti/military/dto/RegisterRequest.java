package ua.edu.viti.military.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username обов'язковий")
    @Size(min = 3, max = 50, message = "Username повинен бути від 3 до 50 символів")
    private String username;

    @NotBlank(message = "Email обов'язковий")
    @Email(message = "Некоректний формат email")
    private String email;

    @NotBlank(message = "Пароль обов'язковий")
    @Size(min = 6, message = "Пароль повинен містити мінімум 6 символів")
    private String password;

    private String fullName;

    private String militaryRank;
}
