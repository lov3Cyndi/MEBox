package org.cyndi.backend.service;

import org.cyndi.backend.dto.AuthResponse;
import org.cyndi.backend.dto.LoginRequest;
import org.cyndi.backend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    void resetPassword(String username, String newPassword);
}
