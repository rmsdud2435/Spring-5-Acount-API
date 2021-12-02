package com.naver.line.demo.user;

import static com.naver.line.demo.utils.ApiUtils.success;

import javax.validation.Valid;

import com.naver.line.demo.common.exceptions.NotFoundException;
import com.naver.line.demo.user.dto.CreateUserRequest;
import com.naver.line.demo.user.dto.UpdateUserRequest;
import com.naver.line.demo.user.dto.UserDto;
import com.naver.line.demo.user.entities.User;
import com.naver.line.demo.utils.ApiUtils.ApiResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;
  
  @PostMapping
  public ApiResult<UserDto> create(
    @Valid @RequestBody CreateUserRequest body
  ) {
    User user = userService.create(body.getName(), body.getEmail(), body.getPhone(), body.getBirthday());
    return success(new UserDto(user));
  }

  @PutMapping
  public ApiResult<UserDto> update(
    @RequestHeader("X-USER-ID") Integer userId,
    @Valid @RequestBody UpdateUserRequest body
  ) {
    User user = userService.update(userId, body.getName(), body.getEmail(), body.getPhone(), body.getBirthday());
    return success(new UserDto(user));
  }

  @GetMapping
  public ApiResult<UserDto> getOne(
    @RequestHeader("X-USER-ID") Integer userId
  ) {
    return success(
      this.userService.findById(userId)
        .map(UserDto::new)
        .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."))
    );
  }

  @DeleteMapping
  public ApiResult<UserDto> delete(
    @RequestHeader("X-USER-ID") Integer userId
  ) {
    User user = userService.delete(userId);
    return success(new UserDto(user));
  }
}
