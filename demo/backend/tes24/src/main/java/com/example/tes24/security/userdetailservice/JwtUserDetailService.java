package com.example.tes24.security.userdetailservice;

import com.example.tes24.entity.Member;
import com.example.tes24.repository.MemberRepository;
import com.example.tes24.security.userdetails.JwtUserDetails;
import com.example.tes24.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service("jwtUserDetailService")
@RequiredArgsConstructor
public class JwtUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(id)).orElseThrow(NoSuchElementException::new);
        return new JwtUserDetails(member.getMemberId(), member.getAccessToken(), member.getRefreshToken());
    }
}
