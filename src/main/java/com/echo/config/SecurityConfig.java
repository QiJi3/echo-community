package com.echo.config;

import com.echo.security.RestAccessDeniedHandler;
import com.echo.security.RestAuthenticationEntryPoint;
import com.echo.security.RestRememberMeServices;
import com.echo.service.UserService;
import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private static final String REMEMBER_ME_KEY = "echo-remember-me-key";

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(
      UserService userService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
    return new ProviderManager(provider);
  }

  @Bean
  public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
    repository.setDataSource(dataSource);
    return repository;
  }

  @Bean
  public RestRememberMeServices rememberMeServices(
      UserService userService, PersistentTokenRepository tokenRepository) {
    RestRememberMeServices rememberMeServices =
        new RestRememberMeServices(REMEMBER_ME_KEY, userService, tokenRepository);
    rememberMeServices.setParameter("rememberMe");
    rememberMeServices.setTokenValiditySeconds((int) Duration.ofDays(7).getSeconds());
    rememberMeServices.setAlwaysRemember(false);
    return rememberMeServices;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      DaoAuthenticationProvider daoAuthenticationProvider,
      RestRememberMeServices rememberMeServices,
      UserService userService,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler)
      throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.formLogin(fl -> fl.disable());
    http.httpBasic(hb -> hb.disable());

    http.authenticationProvider(daoAuthenticationProvider);
    http.exceptionHandling(
        e ->
            e.authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));
    http.sessionManagement(
        s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
    http.authorizeHttpRequests(
        auth ->
            auth.requestMatchers("/error")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/captcha")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/post/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/followees", "/api/followers")
                .permitAll()
                .requestMatchers(
                    HttpMethod.GET, "/api/columns/**", "/api/interviews/**", "/api/moments/**")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/post")
                .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/message")
                .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/like", "/api/follow")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/message/**", "/api/notification/**")
                .authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/post/*/top", "/api/post/*/highlight")
                .hasAnyRole("MODERATOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/post/*")
                .hasRole("ADMIN")
                .requestMatchers("/api/v1/admin/**")
                .hasRole("ADMIN")
                .requestMatchers("/api/v1/mod/**")
                .hasAnyRole("MODERATOR", "ADMIN")
                .requestMatchers("/api/v1/user/**")
                .hasAnyRole("USER", "MODERATOR", "ADMIN")
                .anyRequest()
                .authenticated());
    http.rememberMe(
        rm ->
            rm.rememberMeServices(rememberMeServices)
                .key(REMEMBER_ME_KEY)
                .userDetailsService(userService));
    return http.build();
  }
}
