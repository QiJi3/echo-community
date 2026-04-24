package com.echo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
  @NotBlank(message = "username is required")
  @Size(min = 3, max = 32, message = "username length must be between 3 and 32")
  private String username;

  @NotBlank(message = "password is required")
  @Size(min = 8, max = 64, message = "password length must be between 8 and 64")
  private String password;

  @NotBlank(message = "email is required")
  @Email(message = "email format is invalid")
  private String email;

  private String avatar;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
