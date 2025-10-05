package com.tiket.entitas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entitas Pengguna
 * Menyimpan informasi pengguna sistem tiket
 */
@Entity
@Table(name = "pengguna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pengguna {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nama;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "kata_sandi", nullable = false)
    private String kataSandi;
    
    @Column(nullable = false, length = 50)
    private String peran;
    
    @Column(name = "departemen_id")
    private Integer departemenId;
    
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
}
