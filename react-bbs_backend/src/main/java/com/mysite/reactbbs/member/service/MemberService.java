package com.mysite.reactbbs.member.service;

import com.mysite.reactbbs.jwt.JwtTokenUtil;
import com.mysite.reactbbs.member.dao.MemberDao;
import com.mysite.reactbbs.member.dto.param.CreateMemberParam;
import com.mysite.reactbbs.member.dto.request.JoinRequest;
import com.mysite.reactbbs.member.dto.request.LoginRequest;
import com.mysite.reactbbs.member.dto.response.JoinResponse;
import com.mysite.reactbbs.member.dto.response.LoginResponse;
import com.mysite.reactbbs.member.exception.MemberException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberDao dao;
	private final PasswordEncoder encoder;
//	private final BCryptPasswordEncoder encoder;
	private final AuthenticationManager authenticationManager;

	private final JwtTokenUtil jwtTokenUtil;
	private final UserDetailsService userDetailsService;


//	public MemberService(MemberDao dao, PasswordEncoder encoder,
//		AuthenticationManager authenticationManager,
//		JwtTokenUtil jwtTokenUtil,
//		UserDetailsService userDetailsService) {
//		this.dao = dao;
//		this.encoder = encoder;
//		this.authenticationManager = authenticationManager;
//		this.jwtTokenUtil = jwtTokenUtil;
//		this.userDetailsService = userDetailsService;
//	}

	public HttpStatus checkIdDuplicate(String id) {
		isExistUserId(id);
		return HttpStatus.OK;
	}

	@Transactional
	public JoinResponse join(JoinRequest req) {

		saveMember(req);
		authenticate(req.getId(), req.getPwd());

		return new JoinResponse(req.getId());
	}

	private void saveMember(JoinRequest req) {
		// 아이디 중복 확인
		isExistUserId(req.getId());

		// 패스워드 일치 확인
		checkPwd(req.getPwd(), req.getCheckPwd());

		// 회원 정보 생성
		String encodedPwd = encoder.encode(req.getPwd());
		CreateMemberParam param = new CreateMemberParam(req, encodedPwd);

		Integer result = dao.createMember(param);
		if (result == 0) {
			throw new MemberException("회원 등록을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public LoginResponse login(LoginRequest req) {
		authenticate(req.getId(), req.getPwd());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(req.getId());
		final String token = jwtTokenUtil.generateToken(userDetails);

		System.out.println("인증 성공 토큰 출력 : " + token);
		System.out.println("아이디 출력 : " + req.getId());
		
		return new LoginResponse(token, req.getId());
	}

	private void authenticate(String id, String pwd) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, pwd));
		} catch (DisabledException e) {
			throw new MemberException("인증되지 않은 아이디입니다.", HttpStatus.BAD_REQUEST);
		} catch (BadCredentialsException e) {
			throw new MemberException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}
	}

	private void isExistUserId(String id) {
		Integer result = dao.isExistUserId(id);
		if (result == 1) {
			throw new MemberException("이미 사용중인 아이디입니다.", HttpStatus.BAD_REQUEST);
		}
	}

	private void checkPwd(String pwd, String checkPwd) {
		if (!pwd.equals(checkPwd)) {
			throw new MemberException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}
	}
}




