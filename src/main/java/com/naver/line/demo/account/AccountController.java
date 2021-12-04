package com.naver.line.demo.account;

import static com.naver.line.demo.utils.ApiUtils.success;

import javax.validation.Valid;

import com.naver.line.demo.account.dto.AccountDto;
import com.naver.line.demo.account.dto.CreateAccountRequest;
import com.naver.line.demo.account.dto.UpdateAccoutRequest;
import com.naver.line.demo.account.entities.Account;
import com.naver.line.demo.utils.ApiUtils.ApiResult;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

  private final AccountService accountService;
  
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

    /**
      * 1. 계좌 개설
      */
  @PostMapping
  public ApiResult<AccountDto> create(@RequestHeader("X-USER-ID") Integer userId, @Valid @RequestBody CreateAccountRequest body) {
    Account account = accountService.create(userId, body.getTransferLimit(), body.getDailyTransferLimit());
    return success(new AccountDto(account));
  }
  
  /**
   * 2. 계좌 비활성화
   */
  @DeleteMapping(value="/{id}")
  public ApiResult<AccountDto> delete(@RequestHeader("X-USER-ID") Integer userId, @PathVariable("id") Integer id) {
    Account account = accountService.delete(userId, id);
    return success(new AccountDto(account));
  } 

  /**
   * 3. 계좌 이체 한도 수정
   */
  @PutMapping(value="/{id}/transfer-limit")
  public ApiResult<AccountDto> update(@RequestHeader("X-USER-ID") Integer userId, @PathVariable("id") Integer id, @Valid @RequestBody UpdateAccoutRequest body) {
    Account account = accountService.update(userId, id, body.getTransferLimit(), body.getDailyTransferLimit());
    return success(new AccountDto(account));
  }

  /**
   * 5. 계좌 입출금 내역
   */
  /* @GetMapping(value="{id}/transactions")
  public ApiResult<List<AccountDto>> getOne(
    @RequestHeader("X-USER-ID") Integer userId
  ) {
    return success(
      this.userService.findById(userId)
        .map(UserDto::new)
        .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."))
    );
  } */
}



  

  
