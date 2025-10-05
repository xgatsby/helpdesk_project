package com.helpdesk.service;

import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketPriority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SLAService {

    private static final int URGENT_HOURS = 4;
    private static final int HIGH_HOURS = 8;
    private static final int MEDIUM_HOURS = 24;
    private static final int LOW_HOURS = 48;
    private static final int LOWEST_HOURS = 72;

    public int getSlaHoursForPriority(TicketPriority priority) {
        return switch (priority) {
            case URGENT -> URGENT_HOURS;
            case HIGH -> HIGH_HOURS;
            case MEDIUM -> MEDIUM_HOURS;
            case LOW -> LOW_HOURS;
            case LOWEST -> LOWEST_HOURS;
        };
    }

    public LocalDateTime calculateSlaDueAt(TicketPriority priority) {
        return LocalDateTime.now().plusHours(getSlaHoursForPriority(priority));
    }

    public LocalDateTime calculateSlaDueAt(TicketPriority priority, LocalDateTime createdAt) {
        return createdAt.plusHours(getSlaHoursForPriority(priority));
    }

    public double getSlaProgressPercentage(Ticket ticket) {
        if (ticket.getTimeResolved() != null) {
            return 100.0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(ticket.getSlaDueAt())) {
            return 100.0;
        }

        long totalMinutes = java.time.Duration.between(ticket.getCreatedAt(), ticket.getSlaDueAt()).toMinutes();
        long elapsedMinutes = java.time.Duration.between(ticket.getCreatedAt(), now).toMinutes();

        return Math.min(100.0, (double) elapsedMinutes / totalMinutes * 100);
    }

    public boolean isSlaWarning(Ticket ticket, int warningThreshold) {
        return getSlaProgressPercentage(ticket) >= warningThreshold;
    }

    public boolean isSlaCritical(Ticket ticket, int criticalThreshold) {
        return getSlaProgressPercentage(ticket) >= criticalThreshold;
    }

    public boolean isSlaOverdue(Ticket ticket) {
        return LocalDateTime.now().isAfter(ticket.getSlaDueAt());
    }
}
