package com.huce.webblog.dto.request;

import com.huce.webblog.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    @NotBlank(message = "Name cannot be blank")
    String name;

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email cannot be blank")
    String email;

    @Size(min = 8, max = 20, message = "invalid password")
    String password;

    @Past(message = "Date of birth must be before current date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dob;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;
}