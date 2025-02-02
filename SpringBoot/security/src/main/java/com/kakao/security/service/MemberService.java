package com.kakao.security.service;

import com.kakao.security.dto.ClubMemberJoinDTO;
import com.kakao.security.dto.ClubMemberSecurityDTO;

public interface MemberService {

    // 회원이 존재하는 경우 발생시킬 예외 클래스
    static class MidExistException extends Exception{}

    void join(ClubMemberJoinDTO clubMemberJoinDTO) throws MidExistException;
}
