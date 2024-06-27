package com.demo.monitor_rds.controller;

import com.demo.monitor_rds.service.CloudWatchMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rds")
public class MetricsController {

    private final CloudWatchMetricsService cloudWatchMetricsService;

    public MetricsController(CloudWatchMetricsService cloudWatchMetricsService) {
        this.cloudWatchMetricsService = cloudWatchMetricsService;
    }

    @GetMapping
    public String healthCheck(){
        return "Application running";
    }


    @GetMapping("metrics/all")
    public Map<String, Double> getAllMetrics() {
        return cloudWatchMetricsService.getAllMetrics();
    }
}
