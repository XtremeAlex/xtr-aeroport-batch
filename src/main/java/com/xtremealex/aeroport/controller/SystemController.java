package com.xtremealex.aeroport.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class SystemController {

    //Questo Controller è diventato Obsoleto, meglio usare le metriche di Spring
    //Spring Boot Actuator (Metriche)  http://127.0.0.1:8081/xtr-aeroport-batch/actuator/prometheus

    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.00");

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        systemInfo.put("availableProcessors", availableProcessors);
        log.debug("Numero di processori disponibili per la JVM: " + availableProcessors);

        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        systemInfo.put("maxMemoryMB", df.format(maxMemory));
        log.debug("Memoria massima che la JVM cercherà di usare, convertita in MB: " + df.format(maxMemory));

        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        systemInfo.put("totalMemoryMB", df.format(totalMemory));
        log.debug("Memoria totale attualmente in uso dalla JVM, convertita in MB: " + df.format(totalMemory));

        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        systemInfo.put("freeMemoryMB", df.format(freeMemory));
        log.debug("Memoria libera nella JVM, convertita in MB: " + df.format(freeMemory));

        String osName = System.getProperty("os.name");
        systemInfo.put("osName", osName);
        log.debug("Sistema operativo: " + osName);

        String osArch = System.getProperty("os.arch");
        systemInfo.put("osArch", osArch);
        log.debug("Architettura del sistema operativo: " + osArch);

        String osVersion = System.getProperty("os.version");
        systemInfo.put("osVersion", osVersion);
        log.debug("Vers. del sistema operativo: " + osVersion);

        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (osMXBean instanceof com.sun.management.OperatingSystemMXBean) {
            long totalPhysicalMemory = ((com.sun.management.OperatingSystemMXBean) osMXBean).getTotalMemorySize() / (1024 * 1024);
            long freePhysicalMemory = ((com.sun.management.OperatingSystemMXBean) osMXBean).getFreeMemorySize() / (1024 * 1024);
            systemInfo.put("totalPhysicalMemoryMB", df.format(totalPhysicalMemory));
            systemInfo.put("freePhysicalMemoryMB", df.format(freePhysicalMemory));
            log.debug("Memoria fisica del sistema convertita in MB: " +  df.format(totalPhysicalMemory));
            log.debug("Memoria fisica libera convertita in MB: " + df.format(freePhysicalMemory));
        }

        if (osMXBean instanceof com.sun.management.OperatingSystemMXBean) {
            double processCpuLoad = ((com.sun.management.OperatingSystemMXBean) osMXBean).getProcessCpuLoad() * 100;
            systemInfo.put("processCpuLoadPercentage", df.format(processCpuLoad));
            log.debug("Percentuale di utilizzo del processore: " + df.format(processCpuLoad));
        }

        return ResponseEntity.ok(systemInfo);
    }
}
