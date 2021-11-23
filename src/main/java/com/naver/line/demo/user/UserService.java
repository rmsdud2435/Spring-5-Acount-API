package com.naver.line.demo.user;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.LocalDate;
import java.util.Optional;

import com.naver.line.demo.common.exceptions.NotFoundException;
import com.naver.line.demo.common.exceptions.UnauthorizedException;
import com.naver.line.demo.user.entities.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  
  @Transactional(readOnly = true)
  public Optional<User> findById(int id) {
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public User shouldFindById(int id) {
    return this.findById(id)
      .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없음"));
  }

  @Transactional(readOnly = true)
  public User validateUserStatusAndGet(int id) {
    User user = this.userRepository.findById(id)
      .orElseThrow(() -> new UnauthorizedException("사용자 정보를 찾을 수 없습니다."));
    if (!user.isEnabled()) {
      throw new UnauthorizedException("비활성화 된 사용자 정보입니다.");
    }
    return user;
  }

  public User create(String name, String email, String phone, LocalDate birthday) {
    checkNotNull(name, "이름은 필수입니다.");
    checkNotNull(email, "이메일은 필수입니다.");
    checkNotNull(phone, "전화번호는 필수입니다.");

    User user = new User(name, email, phone, birthday);

    this.userRepository.save(user);

    return user;
  }

  @Transactional
  public User update(int id, String name, String email, String phone, LocalDate birthday) {
    User user = this.shouldFindById(id);

    user.setName(name);
    user.setEmail(email);
    user.setPhone(phone);
    user.setBirthday(birthday);

    this.userRepository.save(user);

    return user;
  }

  @Transactional
  public User delete(int id) {
    User user = this.shouldFindById(id);

    user.disable();

    this.userRepository.save(user);

    return user;
  }
}
