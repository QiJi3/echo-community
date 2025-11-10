package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.auth.AuthResponse;
import com.echo.dto.auth.CaptchaResponse;
import com.echo.dto.auth.LoginRequest;
import com.echo.dto.auth.RegisterRequest;
import com.echo.service.CaptchaService;
import com.echo.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final UserService userService;
  private final CaptchaService captchaService;

  public AuthController(UserService userService, CaptchaService captchaService) {
    this.userService = userService;
    this.captchaService = captchaService;
  }

  @GetMapping("/captcha")
  public Result<CaptchaResponse> captcha() {
    return Result.ok(captchaService.createCaptcha());
  }

  @PostMapping("/register")
  public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return Result.ok(userService.register(request), "Registration succeeded");
  }

  @PostMapping("/login")
  public Result<AuthResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    return Result.ok(userService.login(request, httpRequest, httpResponse), "Login succeeded");
  }

  @PostMapping("/logout")
  public Result<Boolean> logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) {
    userService.logout(request, response, authentication);
    return Result.ok(Boolean.TRUE, "Logout succeeded");
  }
}
