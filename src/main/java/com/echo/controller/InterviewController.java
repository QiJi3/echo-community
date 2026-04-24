package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.interview.InterviewListResponse;
import com.echo.entity.Interview;
import com.echo.service.InterviewService;
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
@RequestMapping("/api/interviews")
public class InterviewController {
  private final InterviewService interviewService;

  public InterviewController(InterviewService interviewService) {
    this.interviewService = interviewService;
  }

  @GetMapping
  public Result<InterviewListResponse> listInterviews(
      @RequestParam(required = false) String company,
      @RequestParam(required = false) String position,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0") int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0") int size) {
    return Result.ok(interviewService.listInterviews(company, position, page, size));
  }

  @GetMapping("/{id}")
  public Result<Interview> getInterviewDetail(
      @PathVariable @Positive(message = "id must be positive") long id) {
    return Result.ok(interviewService.findById(id));
  }
}
