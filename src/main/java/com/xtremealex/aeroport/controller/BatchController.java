package com.xtremealex.aeroport.controller;

import com.xtremealex.aeroport.batch.service.AsyncJobService;
import com.xtremealex.aeroport.batch.service.ProgressService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class BatchController {
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private ProgressService progressService;
    @Autowired
    @Qualifier("importAirportTypeJob")
    private Job importAirportTypeJob;

    @Autowired
    @Qualifier("importAirportJob")
    private Job importAirportJob;

    @Autowired
    private AsyncJobService asyncJobService;

    @GetMapping({"/", "/progress"})
    public void redirectToProgress(HttpServletResponse response) throws IOException {
        response.sendRedirect("progress.html");
    }

    @GetMapping("/startJob/{jobName}")
    public ResponseEntity<Map<String, Object>> startJob(@PathVariable String jobName) {

        // Risposta con i dettagli del job, sarebbe da ritornare un Oggetto, per ora va bene cosi ...
        Map<String, Object> response = new HashMap<>();

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("JobId", String.valueOf(System.currentTimeMillis()))
                    .addDate("date", new Date())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            Job jobToLaunch = getJobByName(jobName);
            if (jobToLaunch == null) {
                response.put("message", "Job non trovato");
                return ResponseEntity.badRequest().body(response);
            }

            if (jobAlreadyExecuted(jobName)) {
                response.put("message", "Job già eseguito");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (!jobExplorer.findRunningJobExecutions(jobToLaunch.getName()).isEmpty()) {
                response.put("message", "Job già in esecuzione");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Avvia il job in modo asincrono (Grazie Luca !!!)
            JobExecution jobExecution = asyncJobService.launchJob(jobToLaunch, jobParameters);
            populateJobExecutionResponse(response, jobExecution);

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            response.put("message", "Errore durante l'avvio del job");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private Job getJobByName(String jobName) {
        return switch (jobName) {
            case "importAirportTypeJob" -> importAirportTypeJob;
            case "importAirportJob" -> importAirportJob;
            default -> null;
        };
    }

    private void populateJobExecutionResponse(Map<String, Object> response, JobExecution jobExecution) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        response.put("jobName", jobExecution.getJobInstance().getJobName());
        response.put("status", "Job avviato con successo");
        response.put("startTime", jobExecution.getStartTime() != null ? jobExecution.getStartTime().format(formatter) : "In attesa di inizio");

        // Nota: endTime e duration saranno disponibili solo dopo la conclusione del job percio ho inserito un default
        if (jobExecution.getEndTime() != null) {
            response.put("endTime", jobExecution.getEndTime().format(formatter));
            response.put("duration", calculateDurationText(jobExecution.getStartTime(), jobExecution.getEndTime()));
        } else {
            response.put("endTime", "In esecuzione");
            response.put("duration", "In calcolo");
        }
    }

    private String calculateDurationText(LocalDateTime startTime, LocalDateTime endTime) {
        long durationSeconds = Duration.between(startTime, endTime).getSeconds();
        return durationSeconds < 60 ? durationSeconds + " secondi" : (durationSeconds / 60) + " minuti";
    }

    public boolean jobAlreadyExecuted(String jobName) {
        // Questo serve per ottenere tutte le esecuzioni per il job specificato.
        List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, Integer.MAX_VALUE);
        for (JobInstance instance : instances) {
            // Per ogni istanza del job verifica se trovi qualcuno eseguito
            List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
            for (JobExecution execution : executions) {
                if (execution.getStatus() == BatchStatus.COMPLETED) {
                    // Se c'è un'esecuzione completata con successo, il job è già stato eseguito.
                    return true;
                }
            }
        }
        // Se non ci sono esecuzioni completate con successo, il job viene segnato come non è eseguito.
        return false;
    }

    @GetMapping("/jobs/details")
    public ResponseEntity<List<Map<String, Object>>> listJobDetails() {
        List<Map<String, Object>> jobDetails = new ArrayList<>();
        List<String> jobNames = Arrays.asList("importAirportTypeJob", "importAirportJob");

        for (String jobName : jobNames) {
            Map<String, Object> jobInfo = getJobDetails(jobName);
            jobDetails.add(jobInfo);
        }

        return ResponseEntity.ok(jobDetails);
    }

    @GetMapping("/progress/{jobName}")
    public ResponseEntity<Map<String, Object>> getJobProgress(@PathVariable String jobName) {
        Map<String, Object> jobInfo = getJobDetails(jobName);
        if (jobInfo.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(jobInfo);
        }
    }

    private Map<String, Object> getJobDetails(String jobName) {
        Map<String, Object> jobInfo = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        jobInfo.put("name", jobName);

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
        if (!jobInstances.isEmpty()) {
            JobInstance lastInstance = jobInstances.get(0);
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(lastInstance);
            if (!jobExecutions.isEmpty()) {
                JobExecution lastExecution = jobExecutions.get(0);

                populateJobInfo(jobInfo, lastExecution, formatter);
            }
        } else {
            // Valori predefiniti
            jobInfo.put("startTime", "00/00/0000 00:00:00");
            jobInfo.put("endTime", "00/00/0000 00:00:00");
            jobInfo.put("duration", "N/A");
            jobInfo.put("status", "NON AVVIATO");
            jobInfo.put("progress", "0");
        }
        return jobInfo;
    }

    private void populateJobInfo(Map<String, Object> jobInfo, JobExecution lastExecution, DateTimeFormatter formatter) {
        jobInfo.put("startTime", lastExecution.getStartTime() != null ? lastExecution.getStartTime().format(formatter) : "Mai avviato");
        jobInfo.put("endTime", lastExecution.getEndTime() != null ? lastExecution.getEndTime().format(formatter) : "Non applicabile");
        long durationSeconds = lastExecution.getEndTime() != null ? Duration.between(lastExecution.getStartTime(), lastExecution.getEndTime()).getSeconds() : 0;

        String durationText;
        if (durationSeconds < 2) {
            durationText = "Quanto un gatto in tangenziale";
        } else {
            durationText = durationSeconds < 60 ? durationSeconds + " secondi" : (durationSeconds / 60) + " minuti";
        }

        jobInfo.put("duration", lastExecution.getEndTime() != null ? durationText : "Non applicabile");
        jobInfo.put("status", lastExecution.getStatus().toString());

        if (lastExecution.getStatus().equals(BatchStatus.FAILED)) {
            jobInfo.put("error", lastExecution.getExitStatus().getExitDescription());
        }

        // Viene aggiunta qui la percentuale che viene dal listener
        Double progress = progressService.getProgress(lastExecution.getJobInstance().getJobName());
        jobInfo.put("progress", progress != null ? new DecimalFormat("00.00").format(progress) : "0");
    }
}