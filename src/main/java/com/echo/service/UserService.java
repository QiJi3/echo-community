package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.auth.AuthResponse;
import com.echo.dto.auth.LoginRequest;
import com.echo.dto.auth.RegisterRequest;
import com.echo.entity.User;
import com.echo.mapper.UserMapper;
import com.echo.security.LoginUser;
import com.echo.security.RestRememberMeServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final RestRememberMeServices rememberMeServices;
  private final CaptchaService captchaService;

  public UserService(
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      @Lazy AuthenticationManager authenticationManager,
      @Lazy RestRememberMeServices rememberMeServices,
      CaptchaService captchaService) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.rememberMeServices = rememberMeServices;
    this.captchaService = captchaService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userMapper.selectByUsername(request.getUsername()) != null) {
      throw new ApiException(409, "用户名已被注册");
    }

    User user = new User();
    user.setUsername(request.getUsername().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail().trim());
    user.setAvatar(blankToNull(request.getAvatar()));
    user.setRole("USER");
    user.setStatus(0);

    try {
      int inserted = userMapper.insert(user);
      if (inserted != 1 || user.getId() == null) {
        throw new ApiException(500, "用户创建失败");
      }
    } catch (DuplicateKeyException ex) {
      throw new ApiException(409, "用户名或邮箱已被注册");
    }

    User savedUser = userMapper.selectById(user.getId());
    if (savedUser == null) {
      throw new ApiException(500, "用户注册后加载失败");
    }
    return AuthResponse.from(savedUser);
  }

  public AuthResponse login(
      LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    captchaService.validate(request.getCaptchaId(), request.getCaptchaCode());

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    httpRequest
        .getSession(true)
        .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

    boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());
    rememberMeServices.loginSuccess(httpRequest, httpResponse, authentication, rememberMe);

    LoginUser loginUser = (LoginUser) authentication.getPrincipal();
    User user = userMapper.selectById(loginUser.getUserId());
    if (user == null) {
      throw new ApiException(404, "用户不存在");
    }
    return AuthResponse.from(user);
  }

  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    rememberMeServices.logout(request, response, authentication);
    new SecurityContextLogoutHandler().logout(request, response, authentication);
  }

  @Override
  public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userMapper.selectByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new LoginUser(user);
  }

  private String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
