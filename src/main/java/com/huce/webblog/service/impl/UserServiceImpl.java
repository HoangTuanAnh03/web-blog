package com.huce.webblog.service.impl;

import com.huce.webblog.advice.AppException;
import com.huce.webblog.advice.ErrorCode;
import com.huce.webblog.advice.exception.DuplicateRecordException;
import com.huce.webblog.advice.exception.ResourceNotFoundException;
import com.huce.webblog.dto.NotificationEvent;
import com.huce.webblog.dto.mapper.UserMapper;
import com.huce.webblog.dto.request.CreateUserRequest;
import com.huce.webblog.dto.request.LockRequest;
import com.huce.webblog.dto.request.PasswordCreationRequest;
import com.huce.webblog.dto.request.UpdateUserRequest;
import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.dto.response.UserResponse;
import com.huce.webblog.entity.User;
import com.huce.webblog.entity.VerificationCode;
import com.huce.webblog.repository.UserRepository;
import com.huce.webblog.service.UserService;
import com.huce.webblog.service.VerifyCodeService;
import com.huce.webblog.util.SecurityUtil;
import com.huce.webblog.util.constant.VerifyTypeEnum;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    VerifyCodeService verifyCodeService;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    SecurityUtil securityUtil;
    KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * @param email - Input email
     * @return boolean indicating if the email already exited or not
     */
    @Override
    public boolean isEmailExistAndActive(String email) {
        return this.userRepository.existsByEmailAndActive(email, true);
    }

    /**
     * @param id - Input UserId
     * @return User Object on a given userId
     */
    @Override
    public User fetchUserById(String id) {
        return this.userRepository.findById(id).orElse(null);
    }

    /**
     * @return UserResponse Object - Info currentUser
     */
    @Override
    public UserResponse fetchMyInfo() {
        String email = securityUtil.getCurrentUserLogin().orElse(null);
        return this.userMapper.toUserResponse(Objects.requireNonNull(userRepository.findByEmail(email).orElse(null)));
    }

    /**
     * @param request - password
     */
    @Override
    public void createPassword(PasswordCreationRequest request) {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (StringUtils.hasText(user.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_EXISTED);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /**
     * @param email - Input email
     * @return User Object on a given email
     */
    @Override
    public User fetchUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    /**
     * @param id - Input UserId
     * @return User Details based on a given data updated to database
     */
    @Override
    public UserResponse fetchResUserDtoById(String id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", id)
        );

        return this.userMapper.toUserResponse(user);
    }


    /**
     * @param createUserRequest - Input CreateUserRequest Object
     * @return User Details based on a given data saved to database
     */
    @Transactional
    @Override
    public UserResponse handleCreateUser(CreateUserRequest createUserRequest) {
        User user = userRepository.findByEmail(createUserRequest.getEmail()).orElse(null);
        if (user != null) {
            if (user.getActive()) throw new DuplicateRecordException("USER ", "Email", createUserRequest.getEmail());
            else userRepository.delete(user);
        }

        User newUser = User.builder()
                .name(createUserRequest.getName())
                .gender(createUserRequest.getGender())
                .dob(createUserRequest.getDob())
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .active(false)
                .role("USER")
                .isLocked(false)
                .build();

        VerificationCode verificationCode = verifyCodeService.findByEmail(createUserRequest.getEmail());

        if (verificationCode != null) {
            verifyCodeService.delete(verificationCode);
        }

        String code = RandomStringUtils.randomAlphanumeric(64);

        verifyCodeService.save(VerificationCode.builder()
                .code(code)
                .type(VerifyTypeEnum.REGISTER)
                .email(createUserRequest.getEmail())
                .exp(LocalDateTime.now()).build());

        userRepository.save(newUser);

        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        param.put("name", newUser.getName());
        param.put("email", newUser.getEmail());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(newUser.getEmail())
                .subject("Welcome to ITviec")
                .param(param)
                .body("Hello, " + newUser.getName())
                .build();

        // Publish message to kafka
        kafkaTemplate.send("email_register", notificationEvent);

        return this.userMapper.toUserResponse(newUser);
    }

    /**
     * @param id - Input UserId
     * @param updateUserRequest - Input UpdateUserRequest Object
     * @return User Details based on a given data updated to database
     */
    @Override
    public UserResponse handleUpdateUser(String id, UpdateUserRequest updateUserRequest) {
        User currentUser = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", id)
        );

        currentUser.setName(updateUserRequest.getName());
        currentUser.setGender(updateUserRequest.getGender());
        currentUser.setDob(updateUserRequest.getDob());
        currentUser.setAvatar(updateUserRequest.getAvatar());
        currentUser = this.userRepository.save(currentUser);
        return this.userMapper.toUserResponse(currentUser);
    }

    /**
     * @param id          - Input UserId
     * @param lockRequest - Input LockRequest Object
     * @return User Details based on a given data updated to database
     */
    @Override
    public UserResponse handlerLockUser(String id, LockRequest lockRequest) {
        User user = userRepository.findByIdAndActive(id, true).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", id)
        );

        user.setIsLocked(lockRequest.getLock());
        userRepository.save(user);

        return this.userMapper.toUserResponse(user);
    }

    /**
     * @param ids
     * @return
     */
    @Override
    public List<SimpInfoUserResponse> fetchUserByIdIn(List<String> ids) {
        return userRepository.findByIdIn(ids).stream().map(userMapper::toSimpInfoUserResponse).toList();
    }

    /**
     * @param email
     * @return
     */
    @Override
    public Boolean forgotPassword(String email) {
        User user = userRepository.findFirstByEmailAndActiveAndIsLocked(email, true, false).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if (!StringUtils.hasText(user.getPassword()))
            throw new AppException(ErrorCode.LOGIN_WITH_GOOGLE);

        VerificationCode verificationCode = verifyCodeService.findByEmail(email);

        if (verificationCode != null) {
            verifyCodeService.delete(verificationCode);
        }

        String code = RandomStringUtils.randomAlphanumeric(64);

        verifyCodeService.save(VerificationCode.builder()
                .code(code)
                .type(VerifyTypeEnum.FORGOT_PASSWORD)
                .email(email)
                .exp(LocalDateTime.now()).build());

        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        param.put("name", user.getName());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(email)
                .subject(email + ", reset your password")
                .param(param)
                .build();

        // Publish message to kafka
        kafkaTemplate.send("email_forgot_password", notificationEvent);
        return true;
    }
}
