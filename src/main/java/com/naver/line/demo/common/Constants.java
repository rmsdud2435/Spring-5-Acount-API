package com.naver.line.demo.common;

import java.util.regex.Pattern;

public abstract class Constants {
  public static final String ACCOUNT_NUMBER_PATTERN_STRING = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$";
  public static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile(ACCOUNT_NUMBER_PATTERN_STRING);
}