package com.naver.line.demo.user.dto;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserRequest {
  @Size(min = 2, max = 10, message = "이름은 최소 2글자에서 최대 10글자까지 가능합니다.")
  @NotBlank(message = "이름은 필수입니다.")
  String name;

  @Email(message = "이메일 형식에 맞지 않습니다.")
  @NotBlank(message = "이메일은 필수입니다.")
  String email;

  @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", message = "전화번호 형식에 맞지 않습니다.")
  @NotBlank(message = "전화번호는 필수입니다.")
  String phone;

  @NotNull(message = "생년월일은 필수입니다.")
  LocalDate birthday;

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public LocalDate getBirthday() {
    return birthday;
  }
}
