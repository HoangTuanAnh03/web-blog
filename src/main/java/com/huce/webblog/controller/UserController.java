package com.huce.webblog.controller;

import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.request.ForgotPasswordRequest;
import com.huce.webblog.dto.request.LockRequest;
import com.huce.webblog.dto.request.PasswordCreationRequest;
import com.huce.webblog.dto.request.UpdateUserRequest;
import com.huce.webblog.dto.response.AuthenticationResponse;
import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.dto.response.UserResponse;
import com.huce.webblog.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/create-password")
    public ApiResponse<?> createPassword(@RequestBody @Valid PasswordCreationRequest request) {
        userService.createPassword(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Password has been created, you could use it to log-in")
                .build();
    }

    @GetMapping("/fetchUserById/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("id") String id) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch user by id")
                .data(this.userService.fetchResUserDtoById(id))
                .build();
    }

    @GetMapping("/fetchUserByIdIn")
    public ApiResponse<List<SimpInfoUserResponse>> fetchUserByIdIn(@RequestParam("ids") List<String> ids) {
        return ApiResponse.<List<SimpInfoUserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch users by ids")
                .data(this.userService.fetchUserByIdIn(ids))
                .build();
    }

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch my info")
                .data(this.userService.fetchMyInfo())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") String id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Update a user")
                .data(this.userService.handleUpdateUser(id, updateUserRequest))
                .build();
    }

    @PutMapping("/lock/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") String id, @Valid @RequestBody LockRequest lockRequest) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lock or Unlock a user")
                .data(this.userService.handlerLockUser(id, lockRequest))
                .build();
    }

    @PostMapping("/forgotPassword")
    ApiResponse<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        userService.forgotPassword(forgotPasswordRequest.getEmail());

        return ApiResponse.<AuthenticationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Verify email register success")
                .data(null)
                .build();
    }
}
