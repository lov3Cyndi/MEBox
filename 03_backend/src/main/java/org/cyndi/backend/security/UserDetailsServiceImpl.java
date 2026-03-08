package org.cyndi.backend.security;

import lombok.RequiredArgsConstructor;
import org.cyndi.backend.entity.User;
import org.cyndi.backend.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            Long userId = Long.parseLong(username);
            user = userMapper.selectById(userId);
        } catch (NumberFormatException e) {
            user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
            );
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return UserDetailsImpl.build(user.getId(), user.getUsername(), user.getPassword());
    }
}
