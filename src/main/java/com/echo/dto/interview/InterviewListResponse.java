package com.echo.dto.interview;

import com.echo.entity.Interview;
import java.util.List;

public class InterviewListResponse {
  private List<Interview> interviews;
  private int total;
  private int page;
  private int size;
  private String company;
  private String position;

  public InterviewListResponse() {}

  public InterviewListResponse(
      List<Interview> interviews, int total, int page, int size, String company, String position) {
    this.interviews = interviews;
    this.total = total;
    this.page = page;
    this.size = size;
    this.company = company;
    this.position = position;
  }

  public List<Interview> getInterviews() {
    return interviews;
  }

  public void setInterviews(List<Interview> interviews) {
    this.interviews = interviews;
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

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}
