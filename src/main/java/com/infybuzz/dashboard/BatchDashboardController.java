package com.infybuzz.dashboard;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/batch")
public class BatchDashboardController {

    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final JobLauncher jobLauncher;
    private final ApplicationContext ctx;

    public BatchDashboardController(JobExplorer jobExplorer,
                                    JobOperator jobOperator,
                                    JobLauncher jobLauncher,
                                    ApplicationContext ctx) {
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.jobLauncher = jobLauncher;
        this.ctx = ctx;
    }

    @GetMapping
    public String home(Model model) {
        List<String> jobNames = jobExplorer.getJobNames();

        // Running executions per job
        Map<String, Collection<JobExecution>> running = new LinkedHashMap<>();
        for (String jobName : jobNames) {
            running.put(jobName, jobExplorer.findRunningJobExecutions(jobName));
        }

        // Recent executions (last ~20 across all jobs)
        List<JobExecution> recent = jobNames.stream()
                .flatMap(name -> jobExplorer.getJobInstances(name, 0, 10).stream())
                .flatMap(inst -> jobExplorer.getJobExecutions(inst).stream())
                .sorted(Comparator.comparing(JobExecution::getCreateTime, Comparator.nullsLast(Date::compareTo)).reversed())
                .limit(20)
                .collect(Collectors.toList());

        model.addAttribute("jobNames", jobNames.stream().sorted().collect(Collectors.toList()));
        model.addAttribute("running", running);
        model.addAttribute("recent", recent);
        model.addAttribute("fmt", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
        return "batch/dashboard";
    }

    @GetMapping("/executions/{executionId}")
    public String execution(@PathVariable long executionId, Model model) {
        JobExecution exec = jobExplorer.getJobExecution(executionId);
        if (exec == null) {
            model.addAttribute("error", "JobExecution " + executionId + " not found");
            return "batch/execution";
        }
        model.addAttribute("exec", exec);
        model.addAttribute("steps", exec.getStepExecutions()
                .stream()
                .sorted(Comparator.comparing(StepExecution::getStartTime, Comparator.nullsLast(Date::compareTo)))
                .collect(Collectors.toList()));
        return "batch/execution";
    }

    // Launch a job by name; params like key1=val1,key2=val2 (auto-adds run.id for uniqueness)
    @PostMapping("/run")
    public String run(@RequestParam String jobName,
                      @RequestParam(required = false) String params,
                      Model model) throws Exception {
        Job job = getJobByName(jobName);
        if (job == null) {
            model.addAttribute("error", "Unknown job: " + jobName);
            return "redirect:/batch";
        }
        JobParametersBuilder b = new JobParametersBuilder();
        if (StringUtils.hasText(params)) {
            for (String pair : params.split(",")) {
                if (!pair.contains("=")) continue;
                String[] kv = pair.split("=", 2);
                b.addString(kv[0].trim(), kv[1].trim());
            }
        }
        b.addLong("run.id", System.currentTimeMillis()); // new instance each time
        JobExecution exec = jobLauncher.run(job, b.toJobParameters());
        return "redirect:/batch/executions/" + exec.getId();
    }

    @PostMapping("/executions/{executionId}/restart")
    public String restart(@PathVariable long executionId, Model model) throws Exception {
        Long newId = jobOperator.restart(executionId);
        return "redirect:/batch/executions/" + newId;
    }

    @PostMapping("/executions/{executionId}/stop")
    public String stop(@PathVariable long executionId) throws Exception {
        jobOperator.stop(executionId);
        return "redirect:/batch/executions/" + executionId;
    }

    private Job getJobByName(String jobName) {
        Map<String, Job> jobs = ctx.getBeansOfType(Job.class);
        for (Map.Entry<String, Job> e : jobs.entrySet()) {
            if (e.getValue().getName().equals(jobName) || e.getKey().equals(jobName)) {
                return e.getValue();
            }
        }
        return null;
    }
}
