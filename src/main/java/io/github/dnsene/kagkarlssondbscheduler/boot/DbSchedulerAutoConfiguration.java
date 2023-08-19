package io.github.dnsene.kagkarlssondbscheduler.boot;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration(proxyBeanMethods = false)
@Import(DbSchedulerConfig.class)
@ConditionalOnProperty(prefix = "db-scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DbSchedulerAutoConfiguration {

}
