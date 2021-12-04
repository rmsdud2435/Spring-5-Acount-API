package com.naver.line.demo.account;

import java.util.Optional;

import com.naver.line.demo.account.entities.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{

    public Optional<Account> findById(Integer id);

    public Optional<Account> findByIdAndUserId(Integer userId, Integer id);

    public int countByUserIdAndStatus(Integer userId, String status);

    public int countByNumber(String number);
    
}
