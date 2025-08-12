package com.huce.webblog.entity;

import com.huce.webblog.util.constant.VerifyTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "verification_code")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String email;

    String code;

    LocalDateTime exp;

    VerifyTypeEnum type;
}
