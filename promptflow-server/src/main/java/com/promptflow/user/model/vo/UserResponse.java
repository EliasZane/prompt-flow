package com.promptflow.user.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String phone;
    private Integer remainingCount;
    private Integer totalUsedCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token; // JWT token
}
