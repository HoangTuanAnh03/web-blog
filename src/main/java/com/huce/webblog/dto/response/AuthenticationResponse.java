package com.huce.webblog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("refresh_token")
    String refreshToken;

    UserLogin user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserLogin {
        String id;
        String email;
        String name;
        String avatar;
        String role;
        Boolean noPassword;
    }
}