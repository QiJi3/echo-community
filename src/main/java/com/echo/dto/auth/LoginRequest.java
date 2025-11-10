package com.echo.dto.auth;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
  @NotBlank(message = "username is required")
  private String username;

  @NotBlank(message = "password is required")
  private String password;

  @NotBlank(message = "captchaId is required")
  private String captchaId;

  @NotBlank(message = "captchaCode is required")
  private String captchaCode;

  private Boolean rememberMe;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCaptchaId() {
    return captchaId;
  }

  public void setCaptchaId(String captchaId) {
    this.captchaId = captchaId;
  }

  public String getCaptchaCode() {
    return captchaCode;
  }

  public void setCaptchaCode(String captchaCode) {
    this.captchaCode = captchaCode;
  }

  public Boolean getRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(Boolean rememberMe) {
    this.rememberMe = rememberMe;
  }
}
