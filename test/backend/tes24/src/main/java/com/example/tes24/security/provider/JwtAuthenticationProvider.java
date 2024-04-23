package com.example.tes24.security.provider;

import com.example.tes24.security.jwt.JwtUtils;
import com.example.tes24.security.token.JwtAuthenticationToken;
import com.example.tes24.security.userdetails.JwtUserDetails;
import com.example.tes24.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtils jwtUtils;
    private final UserDetailsService jwtUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();
        String memberId = jwtUtils.getId(token);
        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(memberId);

        Assert.isInstanceOf(JwtUserDetails.class, userDetails, "Require JwtUserDetails type instance.");

        JwtUserDetails jwtUserDetails = (JwtUserDetails) userDetails;

        if (isValidJwtUserDetails(jwtUserDetails)) {
            return JwtAuthenticationToken.authenticated(jwtUserDetails, token, jwtUserDetails.getAuthorities());
        } else {
            return authentication;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }

    private boolean isValidJwtUserDetails(JwtUserDetails jwtUserDetails) {
        String accessToken = jwtUserDetails.getAccessToken();
        if (jwtUtils.isValid(accessToken)) return true;

        String refreshToken = jwtUserDetails.getRefreshToken();
        if (jwtUtils.isValid(refreshToken)) return true;

        return false;
    }

}
