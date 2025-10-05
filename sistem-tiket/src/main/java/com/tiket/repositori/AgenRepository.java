package com.tiket.repositori;

import com.tiket.entitas.Agen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository untuk entitas Agen
 * Mengelola operasi database untuk agen support
 */
@Repository
public interface AgenRepository extends JpaRepository<Agen, Integer> {
    
    /**
     * Mencari agen berdasarkan departemen
     * @param departemenId ID departemen
     * @return daftar agen dalam departemen tertentu
     */
    List<Agen> findByDepartemenId(Integer departemenId);
    
    /**
     * Mencari agen berdasarkan status
     * @param status status agen (misal: TERSEDIA, SIBUK, OFFLINE)
     * @return daftar agen dengan status tertentu
     */
    List<Agen> findByStatus(String status);
    
    /**
     * Mencari agen berdasarkan status dan departemen
     * @param status status agen
     * @param departemenId ID departemen
     * @return daftar agen dengan status dan departemen tertentu
     */
    List<Agen> findByStatusAndDepartemenId(String status, Integer departemenId);
}
