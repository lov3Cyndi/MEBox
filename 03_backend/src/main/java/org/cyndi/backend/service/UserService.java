package org.cyndi.backend.service;

import org.cyndi.backend.dto.UserUpdateRequest;
import org.cyndi.backend.entity.User;

public interface UserService {
    User getUserById(Long id);
    User updateUser(Long userId, UserUpdateRequest request);
    User getCurrentUser(String username);
}
