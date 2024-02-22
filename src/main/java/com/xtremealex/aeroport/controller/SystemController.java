package com.xtremealex.aeroport.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SystemController {

    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.00");

        //Numero di processori disponibili per la JVM
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        systemInfo.put("availableProcessors", availableProcessors);

        // Memoria massima che la JVM cercherà di usare, convertita in MB
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        systemInfo.put("maxMemoryMB", df.format(maxMemory));

        // Memoria totale attualmente in uso dalla JVM, convertita in MB
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        systemInfo.put("totalMemoryMB", df.format(totalMemory));

        // Memoria libera nella JVM, convertita in MB
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        systemInfo.put("freeMemoryMB", df.format(freeMemory));

        // Sistema operativo
        String osName = System.getProperty("os.name");
        systemInfo.put("osName", osName);

        // Architettura del sistema operativo
        String osArch = System.getProperty("os.arch");
        systemInfo.put("osArch", osArch);

        // Vers. del sistema operativo
        String osVersion = System.getProperty("os.version");
        systemInfo.put("osVersion", osVersion);

        // Memoria fisica del sistema e memoria fisica libera, convertite in MB
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (osMXBean instanceof com.sun.management.OperatingSystemMXBean) {
            long totalPhysicalMemory = ((com.sun.management.OperatingSystemMXBean) osMXBean).getTotalMemorySize() / (1024 * 1024);
            long freePhysicalMemory = ((com.sun.management.OperatingSystemMXBean) osMXBean).getFreeMemorySize() / (1024 * 1024);
            systemInfo.put("totalPhysicalMemoryMB", df.format(totalPhysicalMemory));
            systemInfo.put("freePhysicalMemoryMB", df.format(freePhysicalMemory));
        }

        // Percentuale di utilizzo del processore
        if (osMXBean instanceof com.sun.management.OperatingSystemMXBean) {
            double processCpuLoad = ((com.sun.management.OperatingSystemMXBean) osMXBean).getProcessCpuLoad() * 100;
            systemInfo.put("processCpuLoadPercentage", df.format(processCpuLoad));
        }

        return ResponseEntity.ok(systemInfo);
    }
}
