package com.tiket.entitas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entitas Agen
 * Menyimpan informasi agen support yang menangani tiket
 */
@Entity
@Table(name = "agen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "pengguna_id", nullable = false, unique = true)
    private Integer penggunaId;
    
    @Column(name = "departemen_id", nullable = false)
    private Integer departemenId;
    
    @Column(name = "tiket_maksimal", nullable = false)
    private Integer tiketMaksimal = 5;
    
    @Column(name = "jumlah_tiket_aktif", nullable = false)
    private Integer jumlahTiketAktif = 0;
    
    @Column(length = 50)
    private String status = "TERSEDIA";
    
    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;
    
    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
    }
    
    /**
     * Menghitung persentase beban kerja agen
     * @return persentase beban kerja (0-100)
     */
    public double hitungPersentaseBebanKerja() {
        if (tiketMaksimal == null || tiketMaksimal == 0) {
            return 0.0;
        }
        return (double) jumlahTiketAktif / tiketMaksimal * 100;
    }
}
