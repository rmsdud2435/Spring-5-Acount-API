# 네이버 라인 계좌 관리 API 서버 개발

## 소개

계좌를 개설하고 이체 할 수 있는 API를 개발해야 합니다. 다행히 전임자가 사용자 관리 API를 완성해두었습니다.

기능 요구 사항과 상세 기술 구현 사항을 잘 확인하고, 계좌 관리 API 서버를 완성해주세요.

## 수행 기술

- Java 또는 Kotlin를 활용해서 문제를 풀어주세요.
- 먼저 각 언어별로 branch를 변경하여 풀어주시면 됩니다. (각 언어별 branch에 과제를 시작하기 위한 베이스 코드가 담겨 있습니다.)
  - Java 브랜치명 : java
  - Kotlin 브랜치명 : kotlin

(예시) Java로 진행하는 방법:

1. VSCode에서 터미널을 띄웁니다.
2. 다음 명령어를 입력합니다.

``` bash
# 브랜치 변경
$ git checkout java
```

**(주의) 브랜치 변경은 처음에 언어 선택을 위해 한번만 해주시길 바랍니다.**

- 문제를 풀다가 도중에 브랜치를 변경할 경우 설정과 관련된 문제가 생길 수 있으며, 제출 시점의 브랜치를 기준으로 채점이 이루어지므로 도중에 브랜치를 변경하면 불이익이 있을 수 있습니다.

## 기능 요구 사항

- 계좌 개설, 계좌 비활성화, 계좌 이체 한도 수정, 이체, 거래 내역 조회 API를 구현합니다.
- 요청한 사용자 식별 값은 숫자 형태이며 `X-USER-ID`라는 HTTP Header로 전달됩니다.
- 요청/응답 형태(Content-Type)는 JSON 형식을 사용합니다.
- DB Schema는 [schema-h2.sql](./src/main/resources/schema-h2.sql) 파일에서 확인해주세요. Seed는 [data-h2.sql](./src/main/resources/data-h2.sql) 파일에서 확인해주세요.
  - 파일은 수정할 수 없습니다.
- DB는 H2를 사용하며 DB 값을 별도로 디버깅 하고 싶으실 경우엔 {실행시 제공되는 url}/web/h2-console/ 에 들어가서 connect를 하면 됩니다. 문제를 풀기 위한 필수 작업은 아닙니다.
  - h2-console 연결에 필요한 입력 정보는 아래와 같습니다
    - Dirver Class: org.h2.Driver,
    - User name: sa
    - JDBC URL: jdbc:h2:mem:testdb
- 필요한 라이브러리는 자유롭게 `pom.xml`에 추가해서 사용하면 됩니다.
- 테스트는 `mvn test` 명령어로 실행할 수 있습니다.
  - 실행 시 매번 DB 마이그레이션을 수행합니다.

## 상세 기술 구현 사항

### 공통

- 모든 API는 해더 `X-USER-ID`값이 필수입니다. 값이 없을 경우 `401 Unauthorized`로 응답합니다.
- `X-USER-ID` 해더 값으로 `users` 테이블에서 사용자 정보 가져옵니다.
  - 사용자 정보를 찾을 수 없거나 사용자가 비활성화 상태인 경우 `401 Unauthorized`로 응답합니다.
- ResponseBody에서 DateFormat은 LocalDateTime을 그대로 사용하면 됩니다.
  - 예: `{"created_at": "2021-01-01T01:01:00.000000"}`
- 오류 메시지는 정해진 형식 없이 자유롭게 작성해주세요.

#### 요청

Header

| 항목 | 값 | 설명 |
|--|--|--|
| X-USER-ID | 1 | 사용자 식별값 |
| Content-Type | application/json | JSON 형식 사용 |

#### 응답 

HTTP 상태 코드

| 상태 코드 | 설명 |
|--|--|
| 200 | 성공 |
| 400 | 잘못된 요청 |
| 401 | 미인증 |
| 403 | 권한 없음 |
| 404 | 찾을 수 없음 |
| 500 | 내부 서버 오류 |

Header

| 항목 | 값 | 설명 |
|--|--|--|
| Content-Type | application/json | JSON 형식 사용 |

Response Body

| 이름 | 타입 | 필수 | 설명 |
|--|--|--|--|
| success | boolean | O | 성공 여부 |
| response | object | O | 성공 응답 내용 |
| error | object | O | 에러 응답 내용 |

정상처리 및 오류처리 모두 success 필드를 포함합니다.
- 정상처리라면 true, 오류처리라면 false 값을 출력합니다.

정상처리는 response 필드를 포함하고 error 필드는 null입니다.

오류처리는 error 필드를 포함하고 response 필드는 null입니다. error 필드는 status, message 필드를 포함합니다.
- status : HTTP Response status code 값과 동일한 값을 출력해야 합니다.
- message : 오류 메시지가 출력됩니다.

``` json
// 정상 처리
{
  "success": true,
  "response": {
    "id": 1,
    "user_id": 1,
    "number": "123-12-12345",
    "amount": 0,
    "status": "ENABLED",
    "transfer_limit": 1000000,
    "daily_transfer_limit": 3000000,
    "created_at": "2021-01-01T09:00:00.000000",
    "updated_at": "2021-01-01T09:00:00.000000",
  },
  "error": false,
}

// 오류 처리
{
  "success": false,
  "response": null,
  "error": {
    "status": 400,
    "message": "잘못된 요청입니다."
  }
}
```

### 1. 계좌 개설

- 데이터 검증에 실패했을 경우 `400 Bad Request`로 응답합니다.
- 사용자가 오늘 생성한 계좌가 있을 경우 `400 Bad Request`로 응답합니다.
- 사용자가 이미 3개의 활성화된 계좌가 있을 경우 `400 Bad Request`로 응답합니다.
- `accounts` 테이블에 데이터를 저장합니다.
  - 계좌 상태는 `ENABLED`로 저장합니다.
  - 계좌 번호는 중복되지 않는 형식으로 생성해주세요.
    - 형식: 000-00-00000
- 응답으로 생성된 계좌 정보를 내려줍니다.

**URL: POST /api/accounts**

**Request Body:**
| 이름 | 타입 | 필수 | 설명 | 검증 |
|--|--|--|--|--|
| transfer\_limit | number | O | 1회 이체 한도 | 최소 0 ~ 최대 5,000,000 |
| daily\_transfer\_limit | number | O | 1일 이체 한도 | 최소 0 ~ 최대 10,000,000 |

``` json
{
  "transfer_limit": 1000000, // 100만
  "daily_transfer_limit": 3000000, // 300만
}
```

**Response Body:**
``` json
{
  "success": true,
  "response": {
    "id": 1,
    "user_id": 1,
    "number": "123-12-12345",
    "amount": 0,
    "status": "ENABLED",
    "transfer_limit": 1000000,
    "daily_transfer_limit": 3000000,
    "created_at": "2021-01-01T09:00:00.000000",
    "updated_at": "2021-01-01T09:00:00.000000",
  },
  "error": false,
}
```

### 2. 계좌 비활성화

- `accounts` 테이블에서 계좌 정보를 가져옵니다.
- 계좌를 찾을 수 없을 경우 `404 Not Found`으로 응답합니다.
- 사용자가 계좌의 소유자가 아닌 경우 `403 Forbidden`으로 응답합니다.
- 사용자의 계좌의 상태가 이미 `DISABLED`인 경우 `400 Bad Request`로 응답합니다.
- 사용자의 계좌의 잔액이 0원이 아닌 경우 `400 Bad Request`로 응답합니다.
- 사용자의 계좌의 상태값을 `DISABLED`로 저장합니다.
- 응답으로 비활성화된 계좌 정보를 내려줍니다.

**URL: DELETE /api/accounts/{id}**

**Request Body:**
- 없음

**Response Body:**
``` json
{
  "success": true,
  "response": {
    "id": 1,
    "user_id": 1,
    "number": "123-12-12345",
    "status": "DISABLED",
    "amount": 0,
    "transfer_limit": 1000000,
    "daily_transfer_limit": 3000000,
    "created_at": "2021-01-01T09:00:00.000000",
    "updated_at": "2021-01-01T09:00:00.000000",
  },
  "error": false,
}
```

### 3. 계좌 이체 한도 수정

- 데이터 검증에 실패했을 경우 `400 Bad Request`로 응답합니다.
- `accounts` 테이블에서 계좌 정보를 가져옵니다.
- 계좌를 찾을 수 없을 경우 `404 Not Found`으로 응답합니다.
- 사용자가 계좌의 소유자가 아닌 경우 `403 Forbidden`으로 응답합니다.
- 사용자의 계좌가 비활성화 상태인 경우 `400 Bad Request`로 응답합니다.
- 응답으로 수정된 계좌 정보를 내려줍니다.

**URL: PUT /api/accounts/{id}/transfer-limit**

**Request Body:**
| 이름 | 타입 | 필수 | 설명 | 검증 |
|--|--|--|--|--|
| transfer\_limit | number | O | 1회 이체 한도 | 최소 0 ~ 최대 5,000,000 |
| daily\_transfer\_limit | number | O | 1일 이체 한도 | 최소 0 ~ 최대 10,000,000 |

``` json
{
  "transfer_limit": 1000000, // 1,000,000
  "daily_transfer_limit": 3000000, // 3,000,000
}
```

**Response Body:**
``` json
{
  "success": true,
  "response": {
    "id": 1,
    "user_id": 1,
    "number": "123-12-12345",
    "status": "ENABLED",
    "amount": 0,
    "transfer_limit": 1000000,
    "daily_transfer_limit": 3000000,
    "created_at": "2021-01-01T09:00:00.000000",
    "updated_at": "2021-01-01T09:00:00.000000",
  },
  "error": false,
}
```

### 4. 이체

- 데이터 검증에 실패했을 경우 `400 Bad Request`로 응답합니다.
- `accounts` 테이블에서 계좌 정보를 가져옵니다.
- 출금 계좌나 입금 계좌를 찾을 수 없을 경우 `404 Not Found`으로 응답합니다.
- 사용자가 출금 계좌의 소유자가 아닌 경우 `403 Forbidden`으로 응답합니다.
- 출금 계좌나 입금 계좌가 비활성화 상태인 경우 `400 Bad Request`로 응답합니다.
- 출금액이 출금 계좌의 잔액보다 많은 경우 `400 Bad Request`로 응답합니다.
- 이체 금액이 출금 계좌의 1회 이체 한도를 초과할 경우 `400 Bad Request`로 응답합니다.
- 당일 총 이체 금액과 이체 금액의 합이 출금 계좌의 1일 이체 한도를 초과할 경우 `400 Bad Request`로 응답합니다.
  - 당일 총 이체 금액은 00시 00분 00초 ~ 23시 59분 59초 사이 이체 금액의 총 합을 말합니다.
- 출금 정보를 `balance_transactions` 테이블에 저장합니다.
  - 타입은 `WITHDRAW`으로 저장합니다.
  - 출금 전 계좌의 잔액을 `before_balance_amount`에 저장합니다.
  - `note`에 `sender_note`를 저장합니다. `sender_note` 값이 없을 경우 입금 계좌의 고객명을 저장합니다.
- 출금 계좌의 잔액을 금액만큼 감소시킵니다.
- 입금 정보를 `balance_transactions` 테이블에 저장합니다.
  - 타입은 `DEPOSIT`으로 저장합니다.
  - 입금 전 계좌의 잔액을 `before_balance_amount`에 저장합니다.
  - `note`에 `receiver_note` 값을 저장합니다. `receiver_note` 값이 없을 경우 출금 계좌의 고객명을 저장합니다.
- 입금 계좌의 잔액을 금액만큼 증가시킵니다.
- `transfers` 테이블에 데이터를 저장합니다.
  - 출금 정보 저장 후 생성된 ID를 `withdraw_id`에 저장합니다.
  - 입금 정보 저장 후 생성된 ID를 `deposit_id`에 저장합니다.
- 응답으로 출금 거래 정보를 내려줍니다.

**URL: POST /api/transfers/withdraw**

**Request Body:**
| 이름 | 타입 | 필수 | 설명 | 검수 |
|--|--|--|--|--|
| sender\_account\_number | string | O | 출금 계좌번호 | 000-00-00000 형식 |
| receiver\_account\_number | string | O | 입금 계좌번호 | 000-00-00000 형식 |
| amount | number | O | 금액 | 최소 10 이상 |
| sender\_note | string | | 내통장표시 | 최소 2글자 ~ 최대 10글자 |
| receiver\_note | string | | 받는통장표시 | 최소 2글자 ~ 최대 10글자 |

``` json
{
  "sender_account_number": "123-12-12345",
  "reeiver_account_number": "123-12-12346",
  "amount": 10000,
  "sender_note": "gift",
  "receiver_note": null,
}
```

**Response Body:**
``` json
{
  "success": true,
  "response": {
    "id": 1,
    "account_id": 1,
    "amount": 10000,
    "before_balance_amount": 10000,
    "type": "WITHDRAW",
    "note": "gift",
    "created_at": "2021-01-01T09:00:00.000000",
  },
  "error": false,
}
```

### 5. 거래 내역 조회

- `accounts` 테이블에서 계좌 정보를 가져옵니다.
- 계좌를 찾을 수 없을 경우 `404 Not Found`으로 응답합니다.
- 사용자가 계좌의 소유자가 아닌 경우 `403 Forbidden`으로 응답합니다.
- 사용자의 계좌가 비활성화 상태인 경우 `400 Bad Request`로 응답합니다.
- `balance_transactions` 테이블에서 거래 내역을 가져옵니다.
- `page`, `size` 값이 유효하지 않은 경우 기본값으로 대체합니다.
- `from_date`, `to_date` 값이 유효하지 않은 경우 기본값으로 대체합니다.
  - `to_date`는 오늘보다 미래일 수 없습니다.
  - `from_date`는 `to_date`보다 미래일 수 없습니다.
- 정렬은 최신순(내림차순)으로 정렬해주세요.
- 응답으로 조회된 거래 내역을 내려줍니다.

**URL: GET /api/accounts/{id}/transactions**

**Query Parameters:**
| 이름 | 타입 | 필수 | 설명 | 검수 | 기본값 |
|--|--|--|--|--|--|
| page | number | | page 기반 페이징 처리 파리미터 | 최소 0 ~ 최대 Long.MAX_VALUE | 0 |
| size | number | | 출력할 아이템의 갯수 | 최소 1 ~ 최대 5 | 5 |
| from\_date | date | | 조회 시작 일자 | 형식: yyyy-MM-dd | 조회 종료 일자 (예: 2021-12-01) |
| to\_date | date | | 조회 종료 일자 | 형식: yyyy-MM-dd | 오늘 날짜 (예: 2021-12-01) |

```
GET /api/accounts/1/transactions?page=0&size=5&from_date=2021-10-01&to_date=2021-12-01
```

**Response Body:**
``` json
{
  "success": true,
  "response": [{
    "id": 1,
    "account_id": 1,
    "amount": 10000,
    "before_balance_amount": 0,
    "type": "DEPOSIT",
    "note": "name",
    "created_at": "2021-01-01T09:00:00.000000",
  }],
  "error": false,
}
```