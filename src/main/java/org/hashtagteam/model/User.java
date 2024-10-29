package org.hashtagteam.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
// 사용자 정보
public class User {
    private String userId;              // 사용자 ID
    private String password;            // 비밀번호
    private String name;                // 이름
    private String gender;              // 성별
    private LocalDate birth;            // 생년월일
    private String phone;               // 전화번호
    private String email;               // 이메일
    private String primaryAddr;         // 기본 주소
    private String detailAddr;          // 상세 주소
    private LocalDateTime createdDate;  // 계정 생성일
}
