package com.xtremealex.aeroport.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProgressService {

    private final Map<String, Double> jobProgress = new ConcurrentHashMap<>();
    private final Map<SseEmitter, String> emitters = new ConcurrentHashMap<>();

    public void addEmitter(SseEmitter emitter, String jobId) {
        emitters.put(emitter, jobId);
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }


    public void sendProgressUpdate(String jobName, double progress) {
        jobProgress.put(jobName, progress); // Memorizza il progresso
        emitters.forEach((emitter, jobId) -> {
            if (jobId.equals(jobName)) {
                try {
                    emitter.send(SseEmitter.event().name("progress").data(progress));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        });
    }

    public Double getProgress(String jobName) {
        return jobProgress.get(jobName);
    }
}