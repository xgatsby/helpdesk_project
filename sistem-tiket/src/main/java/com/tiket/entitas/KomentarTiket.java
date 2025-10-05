package com.tiket.entitas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entitas Komentar Tiket
 * Menyimpan komentar atau balasan pada tiket
 */
@Entity
@Table(name = "komentar_tiket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KomentarTiket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tiket_id", nullable = false)
    private Integer tiketId;
    
    @Column(name = "penulis_id", nullable = false)
    private Integer penulisId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String isi;
    
    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;
    
    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
    }
}
