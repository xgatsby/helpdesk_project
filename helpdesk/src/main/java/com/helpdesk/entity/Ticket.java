package com.helpdesk.entity;

import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.NEW;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "created_by", nullable = false)
    private UUID createdById;

    @Column(name = "assignee_id")
    private UUID assigneeId;

    @Column(name = "sla_hours", nullable = false)
    private Integer slaHours;

    @Column(name = "sla_due_at", nullable = false)
    private LocalDateTime slaDueAt;

    @Column(name = "escalation_level", nullable = false)
    private Integer escalationLevel = 0;

    @Column(name = "time_resolved")
    private LocalDateTime timeResolved;

    @Column(name = "time_closed")
    private LocalDateTime timeClosed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdByIdUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", insertable = false, updatable = false)
    private Agent assignee;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(slaDueAt);
    }

    public boolean isResolved() {
        return status == TicketStatus.RESOLVED;
    }

    public boolean isClosed() {
        return status == TicketStatus.CLOSED;
    }

    public double getProgressPercentage() {
        if (timeResolved != null) {
            return 100.0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(slaDueAt)) {
            return 100.0;
        }
        long totalMinutes = java.time.Duration.between(createdAt, slaDueAt).toMinutes();
        long elapsedMinutes = java.time.Duration.between(createdAt, now).toMinutes();
        return Math.min(100.0, (double) elapsedMinutes / totalMinutes * 100);
    }
}
