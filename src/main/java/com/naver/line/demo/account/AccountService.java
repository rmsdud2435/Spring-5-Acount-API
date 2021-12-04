package com.naver.line.demo.account;

import static com.google.common.base.Preconditions.checkNotNull;


import java.util.Optional;

import com.naver.line.demo.account.entities.Account;
import com.naver.line.demo.common.exceptions.NotFoundException;
import com.naver.line.demo.common.exceptions.UnauthorizedException;

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
    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Account shouldFindById(Integer id) {
        return this.findById(id).orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없음"));
    }

    @Transactional(readOnly = true)
    public Optional<Account> findByIdAndUserId(Integer userId, Integer id) {
        return accountRepository.findByIdAndUserId(userId, id);
    }

    @Transactional(readOnly = true)
    public Account shouldFindByIdAndUserId(Integer userId, Integer id) {
        return this.findByIdAndUserId(userId, id).orElseThrow(() -> new NotFoundException("계좌를 찾을 수 없음"));
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
        Account account = this.shouldFindById(id);

        if(account.getUserId() != userId){
            throw new UnauthorizedException("본인 소유의 계좌가 아닙니다.");
        }

        if(account.getStatus().equals("DISABLED")){
            throw new IllegalArgumentException("이미 삭제된 계좌입니다.");
        }

        if(account.getAmount() != 0){
            throw new IllegalArgumentException("계좌에 잔고가 남아 있습니다.");
        }

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

}
