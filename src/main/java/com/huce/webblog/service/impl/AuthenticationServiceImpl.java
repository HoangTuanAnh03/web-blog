package com.huce.webblog.service.impl;

import com.huce.webblog.advice.AppException;
import com.huce.webblog.advice.ErrorCode;
import com.huce.webblog.dto.request.AuthenticationRequest;
import com.huce.webblog.dto.request.InvalidatedTokenRequest;
import com.huce.webblog.dto.response.AuthenticationResponse;
import com.huce.webblog.entity.User;
import com.huce.webblog.repository.UserRepository;
import com.huce.webblog.service.AuthenticationService;
import com.huce.webblog.service.InvalidatedTokenService;
import com.huce.webblog.util.SecurityUtil;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserRepository userRepository;
    InvalidatedTokenService invalidatedTokenService;
    SecurityUtil securityUtil;

    @NonFinal
    @Value("${auth.outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${auth.outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${auth.outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";


    /**
     * @param request -AuthenticationRequest Object
     * @return User Details based on a given email and password
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        // authentication user => override loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var user = userRepository
                .findFirstByEmailAndActiveAndIsLocked(request.getEmail(), true, false)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        System.out.println(user.getName());
        return createAuthenticationResponse(user);
    }

    /**
     * @param refreshToken - refreshToken get from cookie
     */
    @Override
    public void logout(String refreshToken) throws ParseException, JOSEException {
        var signToken = securityUtil.verifyToken(refreshToken, true);

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedTokenRequest invalidatedTokenRequest =
                InvalidatedTokenRequest.builder().id(jit).expiryTime(expiryTime.toInstant()).build();

        invalidatedTokenService.createInvalidatedToken(invalidatedTokenRequest);
    }

    /**
     * @param refreshToken - refreshToken get from cookie
     * @return User Details based on a given refreshToken
     */
    @Override
    public AuthenticationResponse refreshToken(String refreshToken) throws ParseException, JOSEException {
        var signedJWT = securityUtil.verifyToken(refreshToken, true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedTokenRequest invalidatedTokenRequest =
                InvalidatedTokenRequest.builder().id(jit).expiryTime(expiryTime.toInstant()).build();

        invalidatedTokenService.createInvalidatedToken(invalidatedTokenRequest);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        return createAuthenticationResponse(user);
    }

    /**
     * @param user - User Object
     * @return Convert User Object to InfoAuthenticationDTO Object
     */
    @Override
    public AuthenticationResponse createAuthenticationResponse(User user) {
        var accessToken = securityUtil.generateToken(user, false);
        var refreshToken = securityUtil.generateToken(user, true);

        AuthenticationResponse.UserLogin userLogin = AuthenticationResponse.UserLogin.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .noPassword(!StringUtils.hasText(user.getPassword()))
                .role("ROLE_" + user.getRole())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userLogin)
                .build();
    }

}
