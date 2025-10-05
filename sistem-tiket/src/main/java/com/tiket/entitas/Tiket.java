package com.tiket.entitas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entitas Tiket
 * Menyimpan informasi tiket support pelanggan
 */
@Entity
@Table(name = "tiket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tiket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String subjek;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String deskripsi;
    
    @Column(nullable = false, length = 100)
    private String kategori;
    
    @Column(nullable = false, length = 50)
    private String prioritas;
    
    @Column(length = 50)
    private String status = "BARU";
    
    @Column(name = "departemen_id", nullable = false)
    private Integer departemenId;
    
    @Column(name = "dibuat_oleh", nullable = false)
    private Integer dibuatOleh;
    
    @Column(name = "ditugaskan_ke")
    private Integer ditugaskanKe;
    
    @Column(name = "jam_sla", nullable = false)
    private Integer jamSla;
    
    @Column(name = "batas_waktu_sla", nullable = false)
    private LocalDateTime batasWaktuSla;
    
    @Column(name = "waktu_selesai")
    private LocalDateTime waktuSelesai;
    
    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;
    
    @Column(name = "diperbarui_pada")
    private LocalDateTime diperbaruiPada;
    
    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
        diperbaruiPada = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        diperbaruiPada = LocalDateTime.now();
    }
    
    /**
     * Mengecek apakah tiket sudah melewati batas waktu SLA
     * @return true jika terlambat, false jika tidak
     */
    public boolean apakahTerlambat() {
        return LocalDateTime.now().isAfter(batasWaktuSla);
    }
}
