package com.naver.line.demo.account;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import com.naver.line.demo.account.entities.Account;
import com.naver.line.demo.common.exceptions.NotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final JdbcAccountRepository jdbcAccountRepository;

    public AccountService(AccountRepository accountRepository, JdbcAccountRepository jdbcAccountRepository) {
        this.accountRepository = accountRepository;
        this.jdbcAccountRepository = jdbcAccountRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Account> findByIdAndUserId(Integer userId, Integer id) {
        return accountRepository.findByIdAndUserId(userId, id);
    }

    @Transactional(readOnly = true)
    public Account shouldFindByIdAndUserId(Integer userId, Integer id) {
        return this.findByIdAndUserId(userId, id).orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없음"));
    }

    public Account create(Integer userId, int transferLimit, int dailyTransferLimit) {
        checkNotNull(userId, "ID는 필수입니다.");
        checkNotNull(transferLimit, "1회 이체 한도 필수입니다.");
        checkNotNull(dailyTransferLimit, "1일 이체 한도는 필수입니다.");

        int todayAccount = this.jdbcAccountRepository.findByIdAndTodayDate(userId);
        if(todayAccount>0){
            throw new IllegalArgumentException("금일 계좌 생성한 이력이 있습니다.");
        }

        int accountCnt = this.accountRepository.countByUserIdAndStatus(userId,"ENABLED");
        if(accountCnt>2){
            throw new IllegalArgumentException("계좌를 3개 이상 생성할 수 없습니다.");
        }

        String number = "";
        while(true){
            int first;
            int second;
            int third;

            first = (int)(Math.random()*901) + 100;
            second = (int)(Math.random()*91) + 10;
            third = (int)(Math.random()*90001) + 10000;

            number = Integer.toString(first) + "-" + Integer.toString(second) + "-" + Integer.toString(third);
            int exist = this.accountRepository.countByNumber(number);
            if(exist<1){
                break;
            }

        }

        Account account = new Account(userId, number, transferLimit, dailyTransferLimit);
    
        this.accountRepository.save(account);
    
        return account;
    }

    @Transactional
    public Account delete(Integer userId, Integer id) {
        Account account = this.shouldFindByIdAndUserId(userId, id);

        account.disable();

        this.accountRepository.save(account);

        return account;
    }

    @Transactional
    public Account update(Integer userId, Integer id, int transferLimit, int dailyTransferLimit) {
        Account account = this.shouldFindByIdAndUserId(userId, id);

        account.setTransferLimit(transferLimit);
        account.setDailyTransferLimit(dailyTransferLimit);

        this.accountRepository.save(account);

        return account;
    }

/*
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
    checkNotNull(birthday, "생년월일은 필수입니다.");

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
  } */
}
