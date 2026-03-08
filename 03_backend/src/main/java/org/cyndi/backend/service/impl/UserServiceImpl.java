package org.cyndi.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.cyndi.backend.dto.UserUpdateRequest;
import org.cyndi.backend.entity.User;
import org.cyndi.backend.mapper.UserMapper;
import org.cyndi.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getTheme() != null) {
            user.setTheme(request.getTheme());
        }
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User getCurrentUser(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
