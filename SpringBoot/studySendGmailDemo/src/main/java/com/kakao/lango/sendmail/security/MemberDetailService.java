package com.kakao.lango.sendmail.security;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kakao.lango.sendmail.dto.MemberDto;
import com.kakao.lango.sendmail.entity.Member;
import com.kakao.lango.sendmail.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Member> result = memberRepository.findById(username);
		Member member = result.orElseThrow(() -> new UsernameNotFoundException("Cannot find memberName"));
		log.info("MemberDetailsService apiUser-------------------------------------");
		MemberDto dto = new MemberDto(
			member.getMemberName(),
			member.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_USER")));
		log.info(dto);
		return dto;
	}
}
