package com.dividend.dividend.persist.model;

import java.util.List;

import com.dividend.dividend.persist.entity.Member;

import lombok.Data;

public class Auth {

	@Data
	public static class SignIn {
		private String username;
		private String password;
	}


	@Data
	public static class SingUp {
		private String username;
		private String password;
		private List<String> roles;

		public Member toEntity() {
			return Member.builder()
				.username(this.username)
				.password(this.password)
				.roles(this.roles)
				.build();
		}
	}


}
