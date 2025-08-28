package com.huce.webblog.service;

import com.huce.webblog.dto.request.CreateUserRequest;
import com.huce.webblog.dto.request.LockRequest;
import com.huce.webblog.dto.request.PasswordCreationRequest;
import com.huce.webblog.dto.request.UpdateUserRequest;
import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.dto.response.UserResponse;
import com.huce.webblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    /**
     * @param email - Input email
     * @return boolean indicating if the email already exited or not
     */
    boolean isEmailExistAndActive(String email);

    /**
     * @param id - Input UserId
     * @return User Object on a given userId
     */
    User fetchUserById(String id);

    /**
     * @return UserResponse Object - Info currentUser
     */
    UserResponse fetchMyInfo();

    /**
     * @param request - password
     */
    void createPassword(PasswordCreationRequest request);

    /**
     * @param email - Input email
     * @return User Object on a given email
     */
    User fetchUserByEmail(String email);

    /**
     * @param id - Input UserId
     * @return User Details based on a given data updated to database
     */
    UserResponse fetchResUserDtoById(String id);

    /**
     * @param newUser - Input CreateUserRequest Object
     * @return User Details based on a given data saved to database
     */
    UserResponse handleCreateUser(CreateUserRequest newUser);

    /**
     * @param id                - Input UserId
     * @param updateUserRequest - Input UpdateUserRequest Object
     * @return User Details based on a given data updated to database
     */
    UserResponse handleUpdateUser(String id, UpdateUserRequest updateUserRequest);


    /**
     * @param id          - Input UserId
     * @param lockRequest - Input LockRequest Object
     * @return User Details based on a given data updated to database
     */
    UserResponse handlerLockUser(String id, LockRequest lockRequest);

    List<SimpInfoUserResponse> fetchUserByIdIn(List<String> ids);

    Page<UserResponse> getAllUser(String role, int page, int size);

    Boolean forgotPassword(String email);

}
