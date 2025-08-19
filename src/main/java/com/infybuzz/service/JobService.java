package com.infybuzz.service;

import com.infybuzz.request.JobParamsRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {
    @Autowired
    JobLauncher jobLauncher;

    @Qualifier("firstJob")
    @Autowired
    Job firstJob;

    @Qualifier("secondJob")
    @Autowired
    Job secondJob;

    @Async
    public void startJob(String jobName, List<JobParamsRequest> jobParamsRequest){
        Map<String, JobParameter> params=new HashMap<String,JobParameter>();
        params.put("currentTime",new JobParameter(System.currentTimeMillis()));

        jobParamsRequest.stream().forEach(jobParams -> {
                                            params.put(jobParams.getParamKey(),new JobParameter(jobParams.getParamValue()));
                                        });


        JobParameters jobParameters=new JobParameters(params);

        try{
            if (jobName.equals("First Job")) {
                jobLauncher.run(firstJob, jobParameters);
            } else if (jobName.equals("Second Job")) {
                jobLauncher.run(secondJob, jobParameters);
            }
        }catch (Exception e){
            System.out.println("Exception in starting the Job...");
        }
    }
}
