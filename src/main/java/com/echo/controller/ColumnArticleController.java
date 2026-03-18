package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.column.ColumnArticleListResponse;
import com.echo.entity.ColumnArticle;
import com.echo.service.ColumnArticleService;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/columns")
public class ColumnArticleController {
  private final ColumnArticleService columnArticleService;

  public ColumnArticleController(ColumnArticleService columnArticleService) {
    this.columnArticleService = columnArticleService;
  }

  @GetMapping
  public Result<ColumnArticleListResponse> listColumns(
      @RequestParam(required = false) String columnName,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0") int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0") int size) {
    return Result.ok(columnArticleService.listArticles(columnName, page, size));
  }

  @GetMapping("/{id}")
  public Result<ColumnArticle> getColumnDetail(
      @PathVariable @Positive(message = "id must be positive") long id) {
    return Result.ok(columnArticleService.findById(id));
  }
}
