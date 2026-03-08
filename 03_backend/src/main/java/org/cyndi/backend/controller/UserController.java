package org.cyndi.backend.controller;

import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.UserUpdateRequest;
import org.cyndi.backend.entity.User;
import org.cyndi.backend.security.UserDetailsImpl;
import org.cyndi.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(userDetails.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUser(userDetails.getId(), request));
    }
}
