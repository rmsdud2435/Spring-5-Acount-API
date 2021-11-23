package com.naver.line.demo.user;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.naver.line.demo.BaseTest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class UserControllerTests extends BaseTest {
  public UserControllerTests() {
    super("/api/users");
  }

  @Nested
  @DisplayName("유저 생성")
  class Create {
    JSONObject createData() throws JSONException {
      JSONObject data = new JSONObject()
        .put("name", "tester")
        .put("email", "test@email.com")
        .put("phone", "010-1234-5678")
        .put("birthday", "2000-01-01");

      return data;
    }

    @Test
    @DisplayName("실패 - 이메일 형식")
    void invalidEmail() throws Exception {
      JSONObject data = createData();
      data.put("email", "invalid_email");

      failure(reqPost("/", data), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("성공")
    void successTest() throws Exception {
      JSONObject data = createData();
  
      success(reqPost("/", data))
        .andExpect(jsonPath("$.response.id", is(7)))
        .andExpect(jsonPath("$.response.name", is("tester")))
        .andExpect(jsonPath("$.response.email", is("test@email.com")))
        .andExpect(jsonPath("$.response.phone", is("010-1234-5678")))
        .andExpect(jsonPath("$.response.birthday", is("2000-01-01")))
        .andExpect(jsonPath("$.response.status", is("ENABLED")))
      ;
  
      JSONObject userJson = findUserById(7);
      assertNotNull(userJson);
      assertEquals(getString(userJson, "name"), "tester");
      assertEquals(getString(userJson, "email"), "test@email.com");
      assertEquals(getString(userJson, "phone"), "010-1234-5678");
      assertEquals(getString(userJson, "birthday"), "2000-01-01");
      assertEquals(getString(userJson, "status"), "ENABLED");
    }
  }

  @Nested
  @DisplayName("유저 수정")
  class Update {
    JSONObject createData() throws JSONException {
      JSONObject data = new JSONObject()
        .put("name", "tester")
        .put("email", "test@email.com")
        .put("phone", "010-1234-5678")
        .put("birthday", "2001-01-01");

      return data;
    }

    @Test
    @DisplayName("실패 - 이메일 형식")
    void invalidEmail() throws Exception {
      JSONObject data = createData();
      data.put("email", "invalid_email");

      failure(reqPost("/", data), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("성공")
    void successTest() throws Exception {
      JSONObject data = createData();
  
      success(reqPut("/", 6, data))
        .andExpect(jsonPath("$.response.id", is(6)))
        .andExpect(jsonPath("$.response.name", is("tester")))
        .andExpect(jsonPath("$.response.email", is("test@email.com")))
        .andExpect(jsonPath("$.response.phone", is("010-1234-5678")))
        .andExpect(jsonPath("$.response.birthday", is("2001-01-01")))
      ;
  
      JSONObject userJson = findUserById(6);
      assertNotNull(userJson);
      assertEquals(getString(userJson, "name"), "tester");
      assertEquals(getString(userJson, "email"), "test@email.com");
      assertEquals(getString(userJson, "phone"), "010-1234-5678");
      assertEquals(getString(userJson, "birthday"), "2001-01-01");
    }
  }

  @Nested
  @DisplayName("유저 조회")
  class GetOne {
    @Test
    @DisplayName("성공")
    void successTest() throws Exception {
      success(reqGet("/", 2))
        .andExpect(jsonPath("$.response.id", is(2)))
        .andExpect(jsonPath("$.response.name", is("name2")))
        .andExpect(jsonPath("$.response.email", is("email2")))
        .andExpect(jsonPath("$.response.phone", is("phone2")))
        .andExpect(jsonPath("$.response.birthday", is("2000-01-01")))
        .andExpect(jsonPath("$.response.status", is("ENABLED")))
      ;
    }
  }

  @Nested
  @DisplayName("유저 삭제")
  class Delete {
    @Test
    @DisplayName("성공")
    void successTest() throws Exception {
      success(reqDel("/", 4))
        .andExpect(jsonPath("$.response.id", is(4)))
        .andExpect(jsonPath("$.response.status", is("DISABLED")))
      ;

      JSONObject userJson = findUserById(4);
      assertNotNull(userJson);
      assertEquals(getString(userJson, "status"), "DISABLED");
    }
  }
}
