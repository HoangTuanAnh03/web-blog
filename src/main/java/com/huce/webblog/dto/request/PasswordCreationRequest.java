package com.huce.webblog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordCreationRequest {
    @Size(min = 8, max = 20, message = "INVALID_PASSWORD")
    String password;
}