package com.echo.security;

import com.echo.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class LoginUser extends org.springframework.security.core.userdetails.User {
  private final Long userId;
  private final String role;

  public LoginUser(User user) {
    super(
        user.getUsername(),
        user.getPassword(),
        user.getStatus() == null || user.getStatus() == 0,
        true,
        true,
        true,
        AuthorityUtils.createAuthorityList("ROLE_" + normalizeRole(user.getRole())));
    this.userId = user.getId();
    this.role = normalizeRole(user.getRole());
  }

  public Long getUserId() {
    return userId;
  }

  public String getRole() {
    return role;
  }

  private static String normalizeRole(String role) {
    if (role == null || role.isBlank()) {
      return "USER";
    }
    return role.trim().toUpperCase();
  }
}
