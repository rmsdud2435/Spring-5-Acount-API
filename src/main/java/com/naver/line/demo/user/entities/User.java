package com.naver.line.demo.user.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String name;

  @Column
  private String email;

  @Column
  private String phone;

  @Column
  private LocalDate birthday;

  @Column
  private Status status;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public User() { }

  public User(String name, String email, String phone, LocalDate birthday) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.birthday = birthday;
    this.status = Status.ENABLED;
  }

  public static enum Status {
    ENABLED, DISABLED
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDate getBirthday() {
    return birthday;
  }
  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public Status getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public boolean isEnabled() {
    return this.status == Status.ENABLED;
  }

  public void disable() {
    this.status = Status.DISABLED;
  }
}
