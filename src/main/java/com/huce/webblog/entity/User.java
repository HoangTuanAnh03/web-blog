package com.huce.webblog.entity;

import com.huce.webblog.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    String email;

    String password;

    LocalDate dob;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    Boolean active;

    String avatar;

    String role;

    Boolean isLocked;
}
