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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Setter
  @Column
  private String name;

  @Setter
  @Column
  private String email;

  @Setter
  @Column
  private String phone;

  @Column
  private Status status;

  @Setter
  @Column
  private LocalDate birthday;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

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

  public boolean isEnabled() {
    return this.status == Status.ENABLED;
  }

  public void disable() {
    this.status = Status.DISABLED;
  }
}
