package com.helpdesk.repository;

import com.helpdesk.entity.Agent;
import com.helpdesk.enums.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {

    List<Agent> findByDepartmentId(UUID departmentId);

    List<Agent> findByStatus(AgentStatus status);

    @Query("SELECT a FROM Agent a WHERE a.status = :status AND a.activeTicketCount < a.maxConcurrentTickets")
    List<Agent> findAvailableAgents(@Param("status") AgentStatus status);

    @Query("SELECT a FROM Agent a WHERE a.departmentId = :departmentId AND a.status = :status AND a.activeTicketCount < a.maxConcurrentTickets")
    List<Agent> findAvailableAgentsByDepartment(@Param("departmentId") UUID departmentId, @Param("status") AgentStatus status);

    @Query("SELECT a FROM Agent a WHERE a.status = 'AVAILABLE' AND a.activeTicketCount < a.maxConcurrentTickets ORDER BY a.activeTicketCount ASC")
    List<Agent> findAvailableAgentsByWorkload();

    @Query("SELECT a FROM Agent a WHERE a.departmentId = :departmentId AND a.status = 'AVAILABLE' AND a.activeTicketCount < a.maxConcurrentTickets ORDER BY a.activeTicketCount ASC")
    List<Agent> findAvailableAgentsByDepartmentAndWorkload(@Param("departmentId") UUID departmentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Agent a WHERE a.id = :id")
    java.util.Optional<Agent> findByIdForUpdate(@Param("id") UUID id);
}
