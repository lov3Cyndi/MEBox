package org.cyndi.backend.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String role;

    public static UserDetailsImpl build(Long id, String username, String password, String role) {
        return new UserDetailsImpl(id, username, password, role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleStr = (role != null && !role.isEmpty()) ? role : "USER";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleStr));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
