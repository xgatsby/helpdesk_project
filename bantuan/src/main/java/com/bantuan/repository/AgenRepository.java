package com.bantuan.repository;

import com.bantuan.entity.Agen;
import com.bantuan.enums.StatusAgen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgenRepository extends JpaRepository<Agen, UUID> {

    List<Agen> findByDepartemenId(UUID departemenId);

    List<Agen> findByStatus(StatusAgen status);

    @Query("SELECT a FROM Agen a WHERE a.status = :status AND a.jumlahTiketAktif < a.tiketMaksimal")
    List<Agen> findAvailableAgents(@Param("status") StatusAgen status);

    @Query("SELECT a FROM Agen a WHERE a.departemenId = :departemenId AND a.status = :status AND a.jumlahTiketAktif < a.tiketMaksimal")
    List<Agen> findAvailableAgentsByDepartment(@Param("departemenId") UUID departemenId, @Param("status") StatusAgen status);

    @Query("SELECT a FROM Agen a WHERE a.status = 'TERSEDIA' AND a.jumlahTiketAktif < a.tiketMaksimal ORDER BY a.jumlahTiketAktif ASC")
    List<Agen> findAvailableAgentsByWorkload(@Param("status") StatusAgen status);

    @Query("SELECT a FROM Agen a WHERE a.departemenId = :departemenId AND a.status = 'TERSEDIA' AND a.jumlahTiketAktif < a.tiketMaksimal ORDER BY a.jumlahTiketAktif ASC")
    List<Agen> findAvailableAgentsByDepartmentAndWorkload(@Param("departemenId") UUID departemenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Agen a WHERE a.id = :id")
    java.util.Optional<Agen> findByIdForUpdate(@Param("id") UUID id);
}
