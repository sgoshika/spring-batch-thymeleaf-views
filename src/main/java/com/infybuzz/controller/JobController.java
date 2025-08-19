package com.infybuzz.controller;

import com.infybuzz.request.JobParamsRequest;
import com.infybuzz.service.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job/")
public class JobController {

    @Autowired
    JobService jobService;

    @GetMapping("/start/{jobName}")
    public String startJob(@PathVariable String jobName, @RequestBody List<JobParamsRequest> jobParamsRequest) throws Exception{
        jobService.startJob(jobName, jobParamsRequest);
        return "Job Started...";
    }
}
