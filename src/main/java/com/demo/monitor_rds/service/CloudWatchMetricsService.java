package com.demo.monitor_rds.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


@Slf4j
@Service
public class CloudWatchMetricsService {
    private final CloudWatchClient cloudWatchClient;

    public CloudWatchMetricsService(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    public Map<String, Double> getAllMetrics() {
        Map<String, Double> metricsValues = new HashMap<>();
        String dbInstanceIdentifier = "database-2";

        List<String> metricNames = Arrays.asList("ReadIOPS", "WriteIOPS", "DatabaseConnections", "NetworkReceiveThroughput", "NetworkTransmitThroughput",
                "ReplicaLog", "FreeableMemory", "CPUCreditUsage", "CPUCreditBalance");

        try {
            Instant endTime = Instant.now();  // Use current time as end time
            Instant startTime = endTime.minus(Duration.ofMinutes(5));  // Example: 5 minutes ago

            List<MetricDataQuery> metricDataQueries = new ArrayList<>();
            for (int i = 0; i < metricNames.size(); i++) {
                String metricName = metricNames.get(i);
                metricDataQueries.add(
                        MetricDataQuery.builder()
                                .id("m" + (i + 1))  // Unique identifier for each query
                                .metricStat(
                                        MetricStat.builder()
                                                .metric(
                                                        Metric.builder()
                                                                .namespace("AWS/RDS")
                                                                .metricName(metricName)
                                                                .dimensions(
                                                                        Dimension.builder()
                                                                                .name("DBInstanceIdentifier")
                                                                                .value(dbInstanceIdentifier)
                                                                                .build()
                                                                )
                                                                .build()
                                                )
                                                .period(60)
                                                .stat("Average")
                                                .build()
                                )
                                .build()
                );
            }

            metricDataQueries.forEach(item -> log.info(item.toString()));

            GetMetricDataRequest request = GetMetricDataRequest.builder()
                    .startTime(startTime)
                    .endTime(endTime)
                    .metricDataQueries(metricDataQueries)
                    .build();

            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

            for (int i = 0; i < metricNames.size(); i++) {
                List<Double> values = response.metricDataResults().get(i).values();
                metricsValues.put(metricNames.get(i), values.isEmpty() ? null : values.get(0));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            metricNames.forEach(metricName -> metricsValues.put(metricName, -1d));
        }

        return metricsValues;
    }
}

