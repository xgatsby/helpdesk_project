package com.helpdesk.entity;

import com.helpdesk.enums.AgentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @ElementCollection
    private String[] skills;

    @Column(name = "max_concurrent_tickets", nullable = false)
    private Integer maxConcurrentTickets = 5;

    @Column(name = "active_ticket_count", nullable = false)
    private Integer activeTicketCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status = AgentStatus.AVAILABLE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public double getWorkloadPercentage() {
        return maxConcurrentTickets > 0 ? (double) activeTicketCount / maxConcurrentTickets * 100 : 0;
    }

    public boolean isFull() {
        return activeTicketCount >= maxConcurrentTickets;
    }

    public boolean isAvailable() {
        return status == AgentStatus.AVAILABLE && !isFull();
    }

    public void incrementActiveTickets() {
        this.activeTicketCount++;
    }

    public void decrementActiveTickets() {
        this.activeTicketCount = Math.max(0, this.activeTicketCount - 1);
    }
}
