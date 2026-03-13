package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.auth.CaptchaResponse;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {
  private static final int WIDTH = 120;
  private static final int HEIGHT = 40;
  private static final Duration EXPIRE_DURATION = Duration.ofMinutes(2);
  private static final String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

  private final SecureRandom secureRandom = new SecureRandom();
  private final Map<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();

  public CaptchaResponse createCaptcha() {
    cleanupExpiredCaptchas();

    String captchaId = UUID.randomUUID().toString().replace("-", "");
    String captchaCode = randomCode(4);
    long expireAt = System.currentTimeMillis() + EXPIRE_DURATION.toMillis();
    captchaStore.put(captchaId, new CaptchaEntry(captchaCode, expireAt));
    return new CaptchaResponse(captchaId, renderImage(captchaCode));
  }

  public void validate(String captchaId, String captchaCode) {
    cleanupExpiredCaptchas();

    CaptchaEntry entry = captchaStore.remove(captchaId);
    if (entry == null || entry.expireAt() < System.currentTimeMillis()) {
      throw new ApiException(400, "验证码已过期，请重新获取");
    }
    if (!entry.code().equalsIgnoreCase(captchaCode)) {
      throw new ApiException(400, "验证码错误");
    }
  }

  private String randomCode(int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = secureRandom.nextInt(CHARSET.length());
      builder.append(CHARSET.charAt(index));
    }
    return builder.toString();
  }

  private String renderImage(String captchaCode) {
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();
    try {
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setColor(new Color(245, 247, 250));
      graphics.fillRect(0, 0, WIDTH, HEIGHT);
      graphics.setFont(new Font("Arial", Font.BOLD, 24));

      for (int i = 0; i < 5; i++) {
        graphics.setColor(randomColor(120, 220));
        graphics.drawLine(
            secureRandom.nextInt(WIDTH),
            secureRandom.nextInt(HEIGHT),
            secureRandom.nextInt(WIDTH),
            secureRandom.nextInt(HEIGHT));
      }

      for (int i = 0; i < captchaCode.length(); i++) {
        graphics.setColor(randomColor(20, 160));
        int x = 18 + i * 22;
        int y = 28 + secureRandom.nextInt(5);
        graphics.drawString(String.valueOf(captchaCode.charAt(i)), x, y);
      }
    } finally {
      graphics.dispose();
    }

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    } catch (IOException ex) {
      throw new ApiException(500, "Failed to create captcha");
    }
  }

  private Color randomColor(int min, int max) {
    int bound = Math.max(max - min, 1);
    int red = min + secureRandom.nextInt(bound);
    int green = min + secureRandom.nextInt(bound);
    int blue = min + secureRandom.nextInt(bound);
    return new Color(red, green, blue);
  }

  private void cleanupExpiredCaptchas() {
    long currentTime = System.currentTimeMillis();
    captchaStore.entrySet().removeIf(entry -> entry.getValue().expireAt() < currentTime);
  }

  private record CaptchaEntry(String code, long expireAt) {}
}
