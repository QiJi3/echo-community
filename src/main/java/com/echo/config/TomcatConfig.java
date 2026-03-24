package com.echo.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // 强制抛弃 Windows/Java17 默认的 NIO WEPoll 模型（会被 TUN / IPv6 策略阻断本地回环）
        // 改为纯 Windows IOCP 的 Nio2Protocol
        factory.setProtocol("org.apache.coyote.http11.Http11Nio2Protocol");
    }
}
