package com.naver.line.demo.account;

public interface JdbcAccountInterface {
    
    public int findByIdAndTodayDate(Integer userId);
}
