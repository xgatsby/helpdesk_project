package com.tiket.repositori;

import com.tiket.entitas.Tiket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository untuk entitas Tiket
 * Mengelola operasi database untuk tiket support pelanggan
 */
@Repository
public interface TiketRepository extends JpaRepository<Tiket, Integer> {
    
    /**
     * Mencari tiket berdasarkan status
     * @param status status tiket (misal: BARU, DITUGASKAN, SELESAI)
     * @return daftar tiket dengan status tertentu
     */
    List<Tiket> findByStatus(String status);
    
    /**
     * Mencari tiket berdasarkan prioritas
     * @param prioritas prioritas tiket (misal: RENDAH, SEDANG, TINGGI, KRITIS)
     * @return daftar tiket dengan prioritas tertentu
     */
    List<Tiket> findByPrioritas(String prioritas);
    
    /**
     * Mencari tiket yang ditugaskan ke agen tertentu
     * @param agenId ID agen
     * @return daftar tiket yang ditugaskan ke agen
     */
    List<Tiket> findByDitugaskanKe(Integer agenId);
    
    /**
     * Mencari tiket yang dibuat oleh pengguna tertentu
     * @param penggunaId ID pengguna pembuat tiket
     * @return daftar tiket yang dibuat oleh pengguna
     */
    List<Tiket> findByDibuatOleh(Integer penggunaId);
    
    /**
     * Mencari tiket berdasarkan departemen
     * @param departemenId ID departemen
     * @return daftar tiket dalam departemen tertentu
     */
    List<Tiket> findByDepartemenId(Integer departemenId);
}
