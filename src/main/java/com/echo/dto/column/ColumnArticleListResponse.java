package com.echo.dto.column;

import com.echo.entity.ColumnArticle;
import java.util.List;

public class ColumnArticleListResponse {
  private List<ColumnArticle> articles;
  private int total;
  private int page;
  private int size;
  private String columnName;

  public ColumnArticleListResponse() {}

  public ColumnArticleListResponse(
      List<ColumnArticle> articles, int total, int page, int size, String columnName) {
    this.articles = articles;
    this.total = total;
    this.page = page;
    this.size = size;
    this.columnName = columnName;
  }

  public List<ColumnArticle> getArticles() {
    return articles;
  }

  public void setArticles(List<ColumnArticle> articles) {
    this.articles = articles;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
}
