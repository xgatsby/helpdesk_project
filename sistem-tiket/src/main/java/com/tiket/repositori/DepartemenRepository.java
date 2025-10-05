package com.tiket.repositori;

import com.tiket.entitas.Departemen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository untuk entitas Departemen
 * Mengelola operasi database untuk departemen
 */
@Repository
public interface DepartemenRepository extends JpaRepository<Departemen, Integer> {
    
    /**
     * Mencari departemen yang namanya mengandung kata tertentu
     * @param nama kata kunci pencarian nama departemen
     * @return daftar departemen yang sesuai
     */
    List<Departemen> findByNamaContaining(String nama);
}
