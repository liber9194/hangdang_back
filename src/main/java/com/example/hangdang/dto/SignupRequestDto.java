package com.example.hangdang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    @NotBlank (message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]*$", message = "아이디는 영문 소문자와 숫자로만 입력해주세요.")
    @Size(min = 4, max = 10, message = "아이디는 4자 이상 10자 이하로 입력해주세요.")
    private String username ;

    @NotBlank (message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_~!@#$%^&*()-+|}{:;'\".,<>?]+$", message = "비밀번호는 영문 대소문자, 숫자, 특수문자로만 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;

    private boolean admin = false;
    private String adminToken = "";

    public String getUsername() { return username; }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public String getAddress() { return address; }

    public boolean isAdmin() {
        return admin;
    }

    public String getAdminToken() {
        return adminToken;
    }
}