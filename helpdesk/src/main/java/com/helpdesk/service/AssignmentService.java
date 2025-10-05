package com.helpdesk.service;

import com.helpdesk.entity.Agent;
import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.AssignmentStrategy;
import com.helpdesk.repository.AgentRepository;
import com.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final SLAService slaService;

    @Value("${assignment.strategy:WORKLOAD_BASED}")
    private AssignmentStrategy defaultStrategy;

    @Transactional
    public Optional<Agent> assignTicket(Ticket ticket) {
        return assignTicket(ticket, defaultStrategy);
    }

    @Transactional
    public Optional<Agent> assignTicket(Ticket ticket, AssignmentStrategy strategy) {
        List<Agent> availableAgents = getAvailableAgents(ticket.getDepartmentId());

        if (availableAgents.isEmpty()) {
            log.warn("No available agents found for department: {}", ticket.getDepartmentId());
            return Optional.empty();
        }

        Agent selectedAgent = switch (strategy) {
            case ROUND_ROBIN -> selectByRoundRobin(availableAgents, ticket);
            case WORKLOAD_BASED -> selectByWorkload(availableAgents);
            case DEPARTMENT_BASED -> selectByDepartment(availableAgents, ticket);
        };

        if (selectedAgent != null) {
            assignTicketToAgent(ticket, selectedAgent);
            log.info("Assigned ticket {} to agent {}", ticket.getId(), selectedAgent.getId());
        }

        return Optional.ofNullable(selectedAgent);
    }

    private List<Agent> getAvailableAgents(UUID departmentId) {
        return agentRepository.findAvailableAgentsByDepartmentAndWorkload(departmentId);
    }

    private Agent selectByWorkload(List<Agent> agents) {
        return agents.stream()
                .min((a1, a2) -> Double.compare(a1.getWorkloadPercentage(), a2.getWorkloadPercentage()))
                .orElse(null);
    }

    private Agent selectByRoundRobin(List<Agent> agents, Ticket ticket) {
        // Simple round-robin based on ticket creation time
        int index = (int) (ticket.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC) % agents.size());
        return agents.get(index);
    }

    private Agent selectByDepartment(List<Agent> agents, Ticket ticket) {
        // For now, return first available agent in department
        // Can be enhanced with skill matching logic
        return agents.stream()
                .filter(agent -> hasRequiredSkills(agent, ticket.getCategory()))
                .findFirst()
                .orElse(agents.stream().findFirst().orElse(null));
    }

    private boolean hasRequiredSkills(Agent agent, String category) {
        if (agent.getSkills() == null) return false;
        return java.util.Arrays.stream(agent.getSkills())
                .anyMatch(skill -> skill.equalsIgnoreCase(category));
    }

    @Transactional
    public void assignTicketToAgent(Ticket ticket, Agent agent) {
        // Use SELECT FOR UPDATE to prevent race conditions
        ticket = ticketRepository.findByIdForUpdate(ticket.getId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        agent = agentRepository.findByIdForUpdate(agent.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        if (agent.isFull()) {
            throw new RuntimeException("Agent has reached maximum concurrent tickets");
        }

        ticket.setStatus(com.helpdesk.enums.TicketStatus.ASSIGNED);
        ticket.setAssigneeId(agent.getId());
        ticketRepository.save(ticket);

        agent.incrementActiveTickets();
        agentRepository.save(agent);
    }

    public void unassignTicket(Ticket ticket) {
        if (ticket.getAssigneeId() != null) {
            Agent agent = agentRepository.findByIdForUpdate(ticket.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));

            ticket.setAssigneeId(null);
            ticket.setStatus(com.helpdesk.enums.TicketStatus.NEW);
            ticketRepository.save(ticket);

            agent.decrementActiveTickets();
            agentRepository.save(agent);
        }
    }

    private boolean hasAgentWithSkills(List<Agent> agents, String category) {
        return agents.stream().anyMatch(agent -> hasRequiredSkills(agent, category));
    }
}
