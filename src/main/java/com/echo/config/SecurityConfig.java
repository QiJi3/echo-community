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
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
    http.csrf().disable();
    http.formLogin().disable();
    http.httpBasic().disable();

    http.authenticationProvider(daoAuthenticationProvider);
    http.exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(accessDeniedHandler);
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    http.authorizeRequests()
        .antMatchers("/error").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v1/auth/captcha").permitAll()
        .antMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
        .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/followees", "/api/followers").permitAll()
        .antMatchers(HttpMethod.GET, "/api/columns/**", "/api/interviews/**", "/api/moments/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/api/post").authenticated()
        .antMatchers(HttpMethod.POST, "/api/message").authenticated()
        .antMatchers(HttpMethod.POST, "/api/like", "/api/follow").authenticated()
        .antMatchers(HttpMethod.GET, "/api/message/**", "/api/notification/**").authenticated()
        .antMatchers(HttpMethod.PUT, "/api/post/*/top", "/api/post/*/highlight")
        .hasAnyRole("MODERATOR", "ADMIN")
        .antMatchers(HttpMethod.DELETE, "/api/post/*").hasRole("ADMIN")
        .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
        .antMatchers("/api/v1/mod/**").hasAnyRole("MODERATOR", "ADMIN")
        .antMatchers("/api/v1/user/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
        .anyRequest().authenticated();
    http.rememberMe()
        .rememberMeServices(rememberMeServices)
        .key(REMEMBER_ME_KEY)
        .userDetailsService(userService);
    return http.build();
  }
}
