package com.naver.line.demo.user.dto;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.naver.line.demo.user.entities.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  private Integer id;
  private String name;
  private String phone;
  private String email;
  private LocalDate birthday;
  private User.Status status;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  public UserDto(User source) {
    copyProperties(source, this);
  }
}
