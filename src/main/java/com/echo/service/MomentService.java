package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.moment.MomentListResponse;
import com.echo.entity.Moment;
import com.echo.mapper.MomentMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MomentService {
  private final MomentMapper momentMapper;

  public MomentService(MomentMapper momentMapper) {
    this.momentMapper = momentMapper;
  }

  public Moment findById(long id) {
    long normalizedId = requirePositiveId(id, "id");
    Moment moment = momentMapper.selectById(normalizedId);
    if (moment == null) {
      throw new ApiException(404, "Moment not found");
    }
    return moment;
  }

  public MomentListResponse listMoments(String topic, int page, int size) {
    String normalizedTopic = normalizeFilter(topic);
    int offset = (page - 1) * size;
    List<Moment> moments = momentMapper.selectList(normalizedTopic, offset, size);
    int total = momentMapper.selectCount(normalizedTopic);
    return new MomentListResponse(moments, total, page, size, normalizedTopic);
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
