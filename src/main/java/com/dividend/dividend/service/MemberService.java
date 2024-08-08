package com.dividend.dividend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dividend.dividend.exception.AlreadyExistUserException;
import com.dividend.dividend.persist.entity.Member;
import com.dividend.dividend.persist.model.Auth;
import com.dividend.dividend.persist.repository.MemberRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return memberRepository.findByUsername(username).orElseThrow(
			() -> new UsernameNotFoundException("유저를 찾을 수 없음 -> " + username
			));

	}

	public Member register(Auth.SingUp member) {
		boolean exists = memberRepository.existsByUsername(member.getUsername());
		if (exists) {
			throw new AlreadyExistUserException();
		}
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		return memberRepository.save(member.toEntity());
	}

	public Member authenticate(Auth.SignIn member) {

		Member user = memberRepository.findByUsername(member.getUsername())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));

		if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}

		return user;
	}
}
