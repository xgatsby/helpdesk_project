package com.tiket.repositori;

import com.tiket.entitas.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk entitas Pengguna
 * Mengelola operasi database untuk pengguna sistem
 */
@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Integer> {
    
    /**
     * Mencari pengguna berdasarkan email
     * @param email alamat email pengguna
     * @return Optional berisi pengguna jika ditemukan
     */
    Optional<Pengguna> findByEmail(String email);
    
    /**
     * Mencari daftar pengguna berdasarkan peran
     * @param peran peran pengguna (misal: ADMIN, AGEN, PELANGGAN)
     * @return daftar pengguna dengan peran tertentu
     */
    List<Pengguna> findByPeran(String peran);
}
