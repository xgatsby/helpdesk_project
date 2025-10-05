package com.helpdesk.controller;

import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.service.AssignmentService;
import com.helpdesk.service.SLAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final AssignmentService assignmentService;
    private final SLAService slaService;

    public TicketController(TicketService ticketService, AssignmentService assignmentService, SLAService slaService) {
        this.ticketService = ticketService;
        this.assignmentService = assignmentService;
        this.slaService = slaService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequest request) {
        Ticket ticket = ticketService.createTicket(
            request.getSubject(),
            request.getDescription(),
            request.getCategory(),
            request.getPriority(),
            request.getDepartmentId(),
            request.getCreatedById()
        );

        // Auto-assign ticket
        assignmentService.assignTicket(ticket);

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable UUID id) {
        Ticket ticket = ticketService.getTicket(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<Page<Ticket>> getTickets(Pageable pageable,
                                           @RequestParam(required = false) TicketStatus status,
                                           @RequestParam(required = false) TicketPriority priority,
                                           @RequestParam(required = false) UUID departmentId) {
        Page<Ticket> tickets = ticketService.getTickets(pageable, status, priority, departmentId);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Ticket> assignTicket(@PathVariable UUID id,
                                       @RequestParam UUID agentId) {
        Ticket ticket = ticketService.assignTicket(id, agentId);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Ticket> closeTicket(@PathVariable UUID id) {
        Ticket ticket = ticketService.closeTicket(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Ticket> resolveTicket(@PathVariable UUID id) {
        Ticket ticket = ticketService.resolveTicket(id);
        return ResponseEntity.ok(ticket);
    }

    static class TicketRequest {
        private String subject;
        private String description;
        private String category;
        private TicketPriority priority;
        private UUID departmentId;
        private UUID createdById;

        // Getters and setters
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public TicketPriority getPriority() { return priority; }
        public void setPriority(TicketPriority priority) { this.priority = priority; }
        public UUID getDepartmentId() { return departmentId; }
        public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
        public UUID getCreatedById() { return createdById; }
        public void setCreatedById(UUID createdById) { this.createdById = createdById; }
    }
}
