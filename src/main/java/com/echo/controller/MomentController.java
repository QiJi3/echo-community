package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.moment.MomentListResponse;
import com.echo.entity.Moment;
import com.echo.service.MomentService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/moments")
public class MomentController {
  private final MomentService momentService;

  public MomentController(MomentService momentService) {
    this.momentService = momentService;
  }

  @GetMapping
  public Result<MomentListResponse> listMoments(
      @RequestParam(required = false) String topic,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0") int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0") int size) {
    return Result.ok(momentService.listMoments(topic, page, size));
  }

  @GetMapping("/{id}")
  public Result<Moment> getMomentDetail(
      @PathVariable @Positive(message = "id must be positive") long id) {
    return Result.ok(momentService.findById(id));
  }
}
