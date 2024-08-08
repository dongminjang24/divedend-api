package com.dividend.dividend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dividend.dividend.persist.entity.Member;
import com.dividend.dividend.persist.model.Auth;
import com.dividend.dividend.security.TokenProvider;
import com.dividend.dividend.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Auth.SingUp request) {
		Member register = memberService.register(request);
		return ResponseEntity.ok(register);
	}

	@PostMapping("/signIn")
	public ResponseEntity<?> signIn(@RequestBody Auth.SignIn request) {
		Member member = memberService.authenticate(request);
		String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());
		return ResponseEntity.ok(token);
	}
}
