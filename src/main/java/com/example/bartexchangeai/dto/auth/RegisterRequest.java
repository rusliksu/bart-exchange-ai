package com.example.bartexchangeai.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username обязателен")
    @Size(min = 3, max = 50, message = "Username должен быть от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 128, message = "Пароль должен быть от 6 до 128 символов")
    private String password;
}
