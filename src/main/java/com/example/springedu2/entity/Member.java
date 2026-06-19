package com.example.springedu2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")  // table 이름을 변경
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Member {  // 회원
    @Id                                                   // Primary Key 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // 번호 자동 증가
    private Long id;

    @Column(nullable = false, unique = true, length = 30) // NOT NULL, UNIQUE, VARCHAR(30)
    private String username;         // 로그인 id

    @Column(nullable = false)        // BCrypt 암호화 통과하면 길이가 길어진다 length 지정안함
    private String password;         // 로그인 비밀번호

    @Column(nullable = false, length = 50)
    private String name;             // 사용자 이름

    @Column(nullable = false)
    private String email;            // 이메일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.USER;   // 권한

    @Column(nullable = false)
    private boolean enabled = true;  // 계정 사용 가능

    @CreationTimestamp               // 자동으로 입력
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 계정 생성일 , 가입일

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 계정 수정일

}
