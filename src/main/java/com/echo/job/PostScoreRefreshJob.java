package com.echo.job;

import com.echo.service.PostService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class PostScoreRefreshJob extends QuartzJobBean {
  private static final Logger log = LoggerFactory.getLogger(PostScoreRefreshJob.class);

  @Autowired private PostService postService;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    int refreshed = postService.refreshPostScores(200);
    log.info("Post score refresh completed, refreshed={}", refreshed);
  }
}
