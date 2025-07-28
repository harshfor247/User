package com.example.User.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    private String userName;
    private String userMobileNumber;
    private String userEmail;
    private Integer userAge;
    private Double userBalance;
}
