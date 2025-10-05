package com.helpdesk.repository;

import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(TicketStatus status);

    org.springframework.data.domain.Page<Ticket> findByStatus(TicketStatus status, org.springframework.data.domain.Pageable pageable);

    List<Ticket> findByAssigneeId(UUID assigneeId);

    org.springframework.data.domain.Page<Ticket> findByDepartmentId(UUID departmentId, org.springframework.data.domain.Pageable pageable);

    List<Ticket> findByDepartmentId(UUID departmentId);

    org.springframework.data.domain.Page<Ticket> findByPriority(com.helpdesk.enums.TicketPriority priority, org.springframework.data.domain.Pageable pageable);

    List<Ticket> findByPriority(com.helpdesk.enums.TicketPriority priority);

    List<Ticket> findByStatusAndDepartmentId(TicketStatus status, UUID departmentId);

    @Query("SELECT t FROM Ticket t WHERE t.status = :status AND t.assigneeId IS NULL")
    List<Ticket> findUnassignedTickets(@Param("status") TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.status IN :statuses AND t.assigneeId IS NULL ORDER BY t.priority DESC, t.createdAt ASC")
    List<Ticket> findUnassignedTicketsByPriorities(@Param("statuses") List<TicketStatus> statuses);

    @Query("SELECT t FROM Ticket t WHERE t.slaDueAt <= :timeNow AND t.status NOT IN :closedStatuses")
    List<Ticket> findOverdueTickets(@Param("timeNow") java.time.LocalDateTime timeNow, @Param("closedStatuses") List<TicketStatus> closedStatuses);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'NEW' OR t.status = 'ASSIGNED' ORDER BY t.priority DESC, t.createdAt ASC")
    Page<Ticket> findActiveTickets(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.assigneeId = :agentId AND (t.status = 'NEW' OR t.status = 'ASSIGNED' OR t.status = 'IN_PROGRESS')")
    List<Ticket> findActiveTicketsByAgent(@Param("agentId") UUID agentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    java.util.Optional<Ticket> findByIdForUpdate(@Param("id") UUID id);
}
