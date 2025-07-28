package com.example.User.dto.response;

import com.example.User.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long userId;
    private UserStatus userStatus;
    private String userName;
    private String userMobileNumber;
    private String userEmail;
    private Integer userAge;
    private Double userBalance;
}
