package org.cyndi.backend.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String avatar;
    private String theme;

    public AuthResponse(String token, Long userId, String username, String email, String avatar, String theme) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.theme = theme;
    }
}
