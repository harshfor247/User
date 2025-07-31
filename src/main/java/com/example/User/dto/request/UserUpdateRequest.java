package com.example.User.dto.request;

import com.example.User.enums.UserStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private Long userId;

    private String userName;
    private String userEmail;
    private String userMobileNumber;
    private Integer userAge;
    private Double userBalance;
}
