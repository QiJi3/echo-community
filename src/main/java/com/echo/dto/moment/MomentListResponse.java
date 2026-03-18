package com.echo.dto.moment;

import com.echo.entity.Moment;
import java.util.List;

public class MomentListResponse {
  private List<Moment> moments;
  private int total;
  private int page;
  private int size;
  private String topic;

  public MomentListResponse() {}

  public MomentListResponse(List<Moment> moments, int total, int page, int size, String topic) {
    this.moments = moments;
    this.total = total;
    this.page = page;
    this.size = size;
    this.topic = topic;
  }

  public List<Moment> getMoments() {
    return moments;
  }

  public void setMoments(List<Moment> moments) {
    this.moments = moments;
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

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }
}
