package com.kakao.security.security;

import com.kakao.security.dto.ClubMemberSecurityDTO;
import com.kakao.security.model.ClubMember;
import com.kakao.security.model.ClubMemberRole;
import com.kakao.security.persistence.ClubMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final ClubMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 카카오 로그인 성공 후 넘어오는 데이터를 이용해서 email을 추출해서 리턴하는 메서드
    private String getKakaoEmail(Map<String, Object> paramMap) {
        // 카카오 계정 정보가 있는 Map을 추출
        Object value = paramMap.get("kakao_account");
        // Linked 는 키의 순서가 있음. HashMap은 키 순서 없음.
        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String) accountMap.get("email");
        log.info("카카오 계정 이메일: " + email);
        return email;
    }

    // email 정보가 있으면 그에 해당하는 DTO를 리턴하고 없으면
    // 회원가입하고 리턴하는 사용자 정의 메서드
    private ClubMemberSecurityDTO generateDTO(String email, Map<String, Object> params) {
        // email을 가지고 데이터 찾아오기
        Optional<ClubMember> result = memberRepository.findByEmail(email);
        if (result.isEmpty()) { // 이메일이 존재하지 않는다면?
            // 회원가입 시도
            ClubMember member = ClubMember.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(ClubMemberRole.USER);
            memberRepository.save(member);
            // 회원 가입에 성공한 정보 리턴
            ClubMemberSecurityDTO memberSecurityDTO = new ClubMemberSecurityDTO(email, "1111", email, null, false, true,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);
            return memberSecurityDTO;
        } else { // 이메일이 존재 한다면?
            ClubMember member = result.get();
            ClubMemberSecurityDTO memberSecurityDTO = new ClubMemberSecurityDTO(
                    member.getMid(), member.getMpw(), member.getEmail(), member.getName(), member.isDel(), member.isSocial(),
                    member.getRoleSet().stream().map(memberRole ->
                        new SimpleGrantedAuthority("ROLE_" + memberRole.name())
                    ).collect(Collectors.toList()));
            return memberSecurityDTO;
        }
    }

    // 로그인 성공했을 때 호출되는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 로그인에 성공한 서비스의 정보 가져오기
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.info("Service Name: " + clientName);

        // 계정에 대한 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;
        switch (clientName.toLowerCase()) {
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }

        return generateDTO(email, paramMap);
    }
}
