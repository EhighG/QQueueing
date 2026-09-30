package com.example.tes24.security.userdetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetails {
    public static final String ID = "id";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    private final Long id;
    private final String accessToken;
    private final String refreshToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return accessToken;
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isCredentialsNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isCredentialsNonExpired();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return refreshToken != null;
    }

    @Override
    public boolean isEnabled() {
        return isCredentialsNonExpired();
    }

}
