package com.example.tes24.service;

import com.example.tes24.entity.Member;

public interface MemberService {
    void delete(Member member);
    Member updateAccessToken(Long memberId, String token);
    Member updateRefreshToken(Long memberId, String token);
    Member signUp(Member member);
    Member login(String memberId);
    Member login(Long memberId);
}
