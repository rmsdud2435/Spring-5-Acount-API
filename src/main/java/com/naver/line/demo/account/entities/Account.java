package com.naver.line.demo.account.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  @Column
  private String number;

  @Column
  private int amount;

  @Column
  private Status status;

  @Column(name = "transfer_limit")
  private int transferLimit;

  @Column(name = "daily_transfer_limit")
  private int dailyTransferLimit;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public Account() { }

  public Account(Integer userId, String number, int transferLimit, int dailyTransferLimit) {
    this.userId = userId;
    this.number = number;
    this.amount = 0;
    this.transferLimit = transferLimit;
    this.dailyTransferLimit = dailyTransferLimit;
    this.status = Status.ENABLED;
  }

  public static enum Status {
    ENABLED, DISABLED
  }

  public boolean isEnabled() {
    return this.status == Status.ENABLED;
  }

  public void disable() {
    this.status = Status.DISABLED;
  }
}
