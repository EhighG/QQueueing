package com.example.tes24.security.provider;

import com.example.tes24.entity.Member;
import com.example.tes24.security.jwt.JwtUtils;
import com.example.tes24.security.token.JwtAuthenticationToken;
import com.example.tes24.security.userdetails.JwtUserDetails;
import com.example.tes24.service.MemberService;
import lombok.RequiredArgsConstructor;
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
    private final MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();
        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(jwtUtils.getId(token));

        Assert.isInstanceOf(JwtUserDetails.class, userDetails, "Require JwtUserDetails type instance.");

        JwtUserDetails jwtUserDetails = (JwtUserDetails) userDetails;

        if (isValidJwtUserDetails(jwtUserDetails)) {
            Long memberId = jwtUserDetails.getId();
            if (!jwtUtils.isValid(jwtUserDetails.getAccessToken())) {
                Member member = memberService.updateAllTokens(memberId, jwtUtils.createAccessToken(memberId), jwtUtils.createRefreshToken(memberId));
                jwtUserDetails = new JwtUserDetails(member.getMemberId(), member.getAccessToken(), member.getRefreshToken());
            }
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
