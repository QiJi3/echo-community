package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.interview.InterviewListResponse;
import com.echo.entity.Interview;
import com.echo.mapper.InterviewMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InterviewService {
  private final InterviewMapper interviewMapper;

  public InterviewService(InterviewMapper interviewMapper) {
    this.interviewMapper = interviewMapper;
  }

  public Interview findById(long id) {
    long normalizedId = requirePositiveId(id, "id");
    Interview interview = interviewMapper.selectById(normalizedId);
    if (interview == null) {
      throw new ApiException(404, "Interview not found");
    }
    return interview;
  }

  public InterviewListResponse listInterviews(String company, String position, int page, int size) {
    String normalizedCompany = normalizeFilter(company);
    String normalizedPosition = normalizeFilter(position);
    int offset = (page - 1) * size;
    List<Interview> interviews =
        interviewMapper.selectList(normalizedCompany, normalizedPosition, offset, size);
    int total = interviewMapper.selectCount(normalizedCompany, normalizedPosition);
    return new InterviewListResponse(interviews, total, page, size, normalizedCompany, normalizedPosition);
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
