package com.tiket.repositori;

import com.tiket.entitas.KomentarTiket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository untuk entitas KomentarTiket
 * Mengelola operasi database untuk komentar pada tiket
 */
@Repository
public interface KomentarTiketRepository extends JpaRepository<KomentarTiket, Integer> {
    
    /**
     * Mencari semua komentar dari tiket tertentu, diurutkan dari yang terbaru
     * @param tiketId ID tiket
     * @return daftar komentar diurutkan berdasarkan waktu pembuatan (descending)
     */
    List<KomentarTiket> findByTiketIdOrderByDibuatPadaDesc(Integer tiketId);
}
