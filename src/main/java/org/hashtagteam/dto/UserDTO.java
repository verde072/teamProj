package org.hashtagteam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
// 사용자 정보 DTO
public class UserDTO {
    private String userId;              // 사용자 ID
    private String name;                // 이름
    private String gender;              // 성별
    private LocalDate birth;            // 생년월일
    private String phone;               // 전화번호
    private String email;               // 이메일
}
