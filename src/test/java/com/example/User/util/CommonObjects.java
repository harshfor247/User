package com.example.User.util;

import com.example.User.dto.request.UserRequest;
import com.example.User.dto.request.UserUpdateRequest;
import com.example.User.dto.response.UserResponse;
import com.example.User.entity.User;
import com.example.User.enums.UserStatus;

public class CommonObjects {

    public static UserRequest sampleUserRequest(){
        return new UserRequest("Alan", "9826198904", "alan@gmail.com", 24, 10000D);
    }

    public static UserResponse sampleUserResponse(){
        return new UserResponse(1L, UserStatus.ACTIVE, "Alan", "9826198904", "alan@gmail.com", 24, 10000D);
    }

    public static User sampleUser(){
        return new User(1L, UserStatus.ACTIVE, "Alan", "9826198904", "alan@gmail.com", 24, 10000D);
    }

    public static UserUpdateRequest sampleUserUpdateRequest(){
        return new UserUpdateRequest(1L, "Alan", "alan@gmail.com","8826198904", 24, 10000D);
    }

    public static UserResponse sampleUserUpdateResponse(){
        return new UserResponse(1L, UserStatus.ACTIVE, "Alan", "8826198904", "alan@gmail.com", 24, 10000D);
    }
}
