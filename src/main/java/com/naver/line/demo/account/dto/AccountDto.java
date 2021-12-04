package com.naver.line.demo.account.dto;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.naver.line.demo.account.entities.Account;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountDto {
    private int id;
    private int userId;
    private String number;
    private int amount;
    private String status;
    @JsonProperty("transfer_limit")
    private int transferLimit;
    @JsonProperty("daily_transfer_limit")
    private int dailyTransferLimit;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public AccountDto(Account source) {
        copyProperties(source, this);
    }
}
