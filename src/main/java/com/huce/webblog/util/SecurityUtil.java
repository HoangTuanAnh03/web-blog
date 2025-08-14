package com.huce.webblog.util;

import com.huce.webblog.advice.AppException;
import com.huce.webblog.advice.ErrorCode;
import com.huce.webblog.entity.User;
import com.huce.webblog.service.InvalidatedTokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityUtil {
    InvalidatedTokenService invalidatedTokenService;

    @NonFinal
    @Value("${auth.jwt.accessSignerKey}")
    String ACCESS_SIGNER_KEY;

    @NonFinal
    @Value("${auth.jwt.refreshSignerKey}")
    String REFRESH_SIGNER_KEY;

    @NonFinal
    @Value("${auth.jwt.valid-duration}")
    long VALID_DURATION;

    @NonFinal
    @Value("${auth.jwt.refreshable-duration}")
    long REFRESHABLE_DURATION;

    /**
     * Get the AccessToken or RefreshToken of the current user.
     *
     * @return the AccessToken or RefreshToken of the current user.
     */
    public String generateToken(User user, boolean isRefresh) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("uid", user.getId())
                .subject(user.getEmail())
                .issuer("tuananhne.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(
                                (isRefresh) ? REFRESHABLE_DURATION : VALID_DURATION,
                                ChronoUnit.SECONDS
                        ).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner((isRefresh) ? REFRESH_SIGNER_KEY.getBytes() : ACCESS_SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify the JWT
     *
     * @return the token validation JWT
     */
    public SignedJWT verifyToken(String token, boolean isRefresh ) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier((isRefresh) ? REFRESH_SIGNER_KEY.getBytes() : ACCESS_SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenService.existById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Build the Scope( Role)
     *
     * @return role
     */
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

//        if (!CollectionUtils.isEmpty((Collection<?>) user.getRole()))
//            user.getRole().forEach(role -> {
        stringJoiner.add("ROLE_" + user.getRole());
//                if (!CollectionUtils.isEmpty(role.getPermissions()))
//                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
//            });

        return stringJoiner.toString();
    }
}
