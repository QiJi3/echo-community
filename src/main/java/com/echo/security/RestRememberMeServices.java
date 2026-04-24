package com.echo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public class RestRememberMeServices extends PersistentTokenBasedRememberMeServices {
  public RestRememberMeServices(
      String key,
      UserDetailsService userDetailsService,
      PersistentTokenRepository tokenRepository) {
    super(key, userDetailsService, tokenRepository);
  }

  public void loginSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication successfulAuthentication,
      boolean rememberMe) {
    if (rememberMe) {
      onLoginSuccess(request, response, successfulAuthentication);
      return;
    }
    cancelCookie(request, response);
  }
}
