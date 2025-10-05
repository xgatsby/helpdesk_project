package com.bantuan.service;

import com.bantuan.enums.PrioritasTiket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SLAService {

    private static final int[] SLA_HOURS = {72, 48, 24, 8, 4}; // TERENDAH, RENDAH, SEDANG, TINGGI, MENDESAK

    public int getSlaHoursForPriority(PrioritasTiket prioritas) {
        return SLA_HOURS[prioritas.ordinal()];
    }

    public LocalDateTime calculateSlaDueAt(PrioritasTiket prioritas) {
        int hours = getSlaHoursForPriority(prioritas);
        return LocalDateTime.now().plusHours(hours);
    }

    public double calculateProgressPercentage(LocalDateTime createdAt, LocalDateTime slaDueAt) {
        if (LocalDateTime.now().isAfter(slaDueAt)) {
            return 100.0;
        }
        long totalMinutes = java.time.Duration.between(createdAt, slaDueAt).toMinutes();
        long elapsedMinutes = java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        return Math.min(100.0, (double) elapsedMinutes / totalMinutes * 100);
    }

    public String getRemainingTime(LocalDateTime slaDueAt) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(slaDueAt)) {
            return "LEWAT WAKTU";
        }
        long minutesLeft = java.time.Duration.between(now, slaDueAt).toMinutes();
        long hours = minutesLeft / 60;
        long minutes = minutesLeft % 60;
        return String.format("%d jam %d menit", hours, minutes);
    }

    public boolean isWarningThreshold(double progress) {
        return progress >= 75.0;
    }

    public boolean isCriticalThreshold(double progress) {
        return progress >= 90.0;
    }

    public boolean isOverdue(LocalDateTime slaDueAt) {
        return LocalDateTime.now().isAfter(slaDueAt);
    }
}
