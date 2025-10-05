package com.helpdesk.service;

import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SLAService slaService;

    @Transactional
    public Ticket createTicket(String subject, String description, String category,
                          TicketPriority priority, UUID departmentId, UUID createdById) {
        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setDepartmentId(departmentId);
        ticket.setCreatedById(createdById);
        ticket.setSlaHours(slaService.getSlaHoursForPriority(priority));
        ticket.setSlaDueAt(slaService.calculateSlaDueAt(priority));

        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Page<Ticket> getTickets(Pageable pageable, TicketStatus status, TicketPriority priority, UUID departmentId) {
        if (status != null && priority != null && departmentId != null) {
            // Complex query with multiple filters - would need custom query
            return ticketRepository.findAll(pageable);
        } else if (status != null) {
            return ticketRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            return ticketRepository.findByPriority(priority, pageable);
        } else if (departmentId != null) {
            return ticketRepository.findByDepartmentId(departmentId, pageable);
        } else {
            return ticketRepository.findAll(pageable);
        }
    }

    @Transactional
    public Ticket assignTicket(UUID ticketId, UUID agentId) {
        Ticket ticket = ticketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setAssigneeId(agentId);
        ticket.setStatus(TicketStatus.ASSIGNED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket resolveTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setTimeResolved(java.time.LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket closeTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setTimeClosed(java.time.LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public void updateTicketStatus(UUID ticketId, TicketStatus status) {
        Ticket ticket = ticketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(status);
        ticketRepository.save(ticket);
    }
}
