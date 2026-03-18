package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.column.ColumnArticleListResponse;
import com.echo.entity.ColumnArticle;
import com.echo.mapper.ColumnArticleMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ColumnArticleService {
  private static final int STATUS_NORMAL = 0;

  private final ColumnArticleMapper columnArticleMapper;

  public ColumnArticleService(ColumnArticleMapper columnArticleMapper) {
    this.columnArticleMapper = columnArticleMapper;
  }

  public ColumnArticle findById(long id) {
    long normalizedId = requirePositiveId(id, "id");
    ColumnArticle article = columnArticleMapper.selectById(normalizedId);
    if (article == null || article.getStatus() == null || article.getStatus() != STATUS_NORMAL) {
      throw new ApiException(404, "Column article not found");
    }
    return article;
  }

  public ColumnArticleListResponse listArticles(String columnName, int page, int size) {
    String normalizedColumnName = normalizeFilter(columnName);
    int offset = (page - 1) * size;
    List<ColumnArticle> articles = columnArticleMapper.selectList(normalizedColumnName, offset, size);
    int total = columnArticleMapper.selectCount(normalizedColumnName);
    return new ColumnArticleListResponse(articles, total, page, size, normalizedColumnName);
  }

  private long requirePositiveId(long id, String fieldName) {
    if (id <= 0) {
      throw new ApiException(400, fieldName + " must be positive");
    }
    return id;
  }

  private String normalizeFilter(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
