package com.echo.config;

import com.echo.job.PostScoreRefreshJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
  @Bean
  public JobDetail postScoreRefreshJobDetail() {
    return JobBuilder.newJob(PostScoreRefreshJob.class)
        .withIdentity("postScoreRefreshJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
    return TriggerBuilder.newTrigger()
        .forJob(postScoreRefreshJobDetail)
        .withIdentity("postScoreRefreshTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 * * * ?"))
        .build();
  }
}
