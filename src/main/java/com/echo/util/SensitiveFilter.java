package com.echo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class SensitiveFilter {
  private static final String REPLACEMENT = "***";

  private final TrieNode rootNode = new TrieNode();

  @PostConstruct
  public void init() {
    ClassPathResource resource = new ClassPathResource("sensitive-words.txt");
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String keyword = line.trim();
        if (!keyword.isEmpty()) {
          addKeyword(keyword);
        }
      }
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to load sensitive words", ex);
    }
  }

  public String filter(String text) {
    if (text == null || text.isBlank()) {
      return text;
    }

    StringBuilder builder = new StringBuilder();
    TrieNode node = rootNode;
    int begin = 0;
    int position = 0;

    while (position < text.length()) {
      char currentChar = text.charAt(position);
      if (isSymbol(currentChar)) {
        if (node == rootNode) {
          builder.append(currentChar);
          begin++;
        }
        position++;
        continue;
      }

      TrieNode nextNode = node.getSubNode(currentChar);
      if (nextNode == null) {
        builder.append(text.charAt(begin));
        position = begin + 1;
        begin = position;
        node = rootNode;
        continue;
      }

      node = nextNode;
      if (node.isKeywordEnd()) {
        builder.append(REPLACEMENT);
        position++;
        begin = position;
        node = rootNode;
        continue;
      }

      position++;
    }

    builder.append(text.substring(begin));
    return builder.toString();
  }

  private void addKeyword(String keyword) {
    TrieNode node = rootNode;
    for (int i = 0; i < keyword.length(); i++) {
      char currentChar = keyword.charAt(i);
      TrieNode nextNode = node.getSubNode(currentChar);
      if (nextNode == null) {
        nextNode = new TrieNode();
        node.addSubNode(currentChar, nextNode);
      }
      node = nextNode;
    }
    node.setKeywordEnd(true);
  }

  private boolean isSymbol(char currentChar) {
    boolean alphaNumeric = Character.isLetterOrDigit(currentChar);
    boolean cjkCharacter = currentChar >= 0x2E80 && currentChar <= 0x9FFF;
    return !alphaNumeric && !cjkCharacter;
  }

  private static class TrieNode {
    private final Map<Character, TrieNode> subNodes = new HashMap<>();
    private boolean keywordEnd;

    public TrieNode getSubNode(Character key) {
      return subNodes.get(key);
    }

    public void addSubNode(Character key, TrieNode node) {
      subNodes.put(key, node);
    }

    public boolean isKeywordEnd() {
      return keywordEnd;
    }

    public void setKeywordEnd(boolean keywordEnd) {
      this.keywordEnd = keywordEnd;
    }
  }
}
