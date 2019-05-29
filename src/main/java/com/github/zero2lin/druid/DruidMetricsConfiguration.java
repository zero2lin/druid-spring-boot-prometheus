package com.github.zero2lin.druid;

import com.alibaba.druid.pool.DruidDataSource;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass({DruidDataSource.class, CollectorRegistry.class})
public class DruidMetricsConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DruidMetricsConfiguration.class);
    private final CollectorRegistry registry;

    public DruidMetricsConfiguration(CollectorRegistry registry) {
        this.registry = registry;
    }

    @Autowired
    public void bindMetricsRegistryToDruidDataSources(Collection<DataSource> dataSources) {
        Map<String, DruidDataSource> druidDataSources = new LinkedHashMap<>();
        for (DataSource dataSource : dataSources) {
            DruidDataSource druidDataSource = DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
            if (druidDataSource != null) {
                druidDataSources.put(druidDataSource.getName(), druidDataSource);
            }
        }

        DruidCollector druidCollector = new DruidCollector(druidDataSources);
        druidCollector.register(registry);
    }
}
