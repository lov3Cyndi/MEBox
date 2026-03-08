package org.cyndi.backend.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private String avatar;
    private String theme;
}
