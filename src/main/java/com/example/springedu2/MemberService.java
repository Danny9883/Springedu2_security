package com.example.springedu2;

import com.example.springedu2.entity.Member;
import com.example.springedu2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 회원정보 db 에서 회원이름으로 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("존재하지 않는 사용자 입니다") );

        // 조회한 결과를 Member -> UserDetails 으로 변환
        UserDetails user = User.builder()
                .username(member.getUsername())       // 사용자 id
                .password(member.getPassword())       // 사용자 비밀번호
                .disabled(!member.isEnabled())        // 계정 사용 가능 disabled(false) -> 사용가능
                .roles(member.getRole().toString())   // 사용자 권한 "ADMIN" -> ROLE_ADMIN 권한
                .build();
        return user;
    }


}
