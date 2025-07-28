package com.example.User.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBalanceUpdatedResponse {
    private Long userId;
    private Double updatedUserBalance;
}
