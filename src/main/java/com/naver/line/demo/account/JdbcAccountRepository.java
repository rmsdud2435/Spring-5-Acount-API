package com.naver.line.demo.account;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAccountRepository implements JdbcAccountInterface{
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findByIdAndTodayDate(Integer userId){
        int count = jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM ACCOUNTS WHERE USER_ID=? AND FORMATDATETIME(created_at,'yyyMMdd') = FORMATDATETIME(now(),'yyyMMdd')", new Object[]{userId}, int.class
        );
        return count;
    };


}
