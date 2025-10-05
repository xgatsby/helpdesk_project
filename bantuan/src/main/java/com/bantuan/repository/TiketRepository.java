package com.bantuan.repository;

import com.bantuan.entity.Tiket;
import com.bantuan.enums.StatusTiket;
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
public interface TiketRepository extends JpaRepository<Tiket, UUID> {

    List<Tiket> findByStatus(StatusTiket status);

    Page<Tiket> findByStatus(StatusTiket status, Pageable pageable);

    List<Tiket> findByDitugaskanKe(UUID ditugaskanKe);

    Page<Tiket> findByDepartemenId(UUID departemenId, Pageable pageable);

    List<Tiket> findByDepartemenId(UUID departemenId);

    Page<Tiket> findByPrioritas(com.bantuan.enums.PrioritasTiket prioritas, Pageable pageable);

    List<Tiket> findByPrioritas(com.bantuan.enums.PrioritasTiket prioritas);

    List<Tiket> findByStatusAndDepartemenId(StatusTiket status, UUID departemenId);

    @Query("SELECT t FROM Tiket t WHERE t.status = :status AND t.ditugaskanKe IS NULL")
    List<Tiket> findUnassignedTickets(@Param("status") StatusTiket status);

    @Query("SELECT t FROM Tiket t WHERE t.status IN :statuses AND t.ditugaskanKe IS NULL ORDER BY t.prioritas DESC, t.dibuatPada ASC")
    List<Tiket> findUnassignedTicketsByPriorities(@Param("statuses") List<StatusTiket> statuses);

    @Query("SELECT t FROM Tiket t WHERE t.batasWaktuSla <= :timeNow AND t.status NOT IN :closedStatuses")
    List<Tiket> findOverdueTickets(@Param("timeNow") java.time.LocalDateTime timeNow, @Param("closedStatuses") List<StatusTiket> closedStatuses);

    @Query("SELECT t FROM Tiket t WHERE t.status = 'BARU' OR t.status = 'DITUGASKAN' OR t.status = 'SEDANG_DIPROSES' ORDER BY t.prioritas DESC, t.dibuatPada ASC")
    Page<Tiket> findActiveTickets(Pageable pageable);

    @Query("SELECT t FROM Tiket t WHERE t.ditugaskanKe = :agenId AND (t.status = 'BARU' OR t.status = 'DITUGASKAN' OR t.status = 'SEDANG_DIPROSES')")
    List<Tiket> findActiveTicketsByAgent(@Param("agenId") UUID agenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tiket t WHERE t.id = :id")
    java.util.Optional<Tiket> findByIdForUpdate(@Param("id") UUID id);
}
