package com.huce.webblog.dto.request;

import com.huce.webblog.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    String name;

    @Past(message = "Date of birth must be before current date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dob;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    String avatar;
}