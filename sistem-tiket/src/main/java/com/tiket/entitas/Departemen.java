package com.tiket.entitas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entitas Departemen
 * Menyimpan informasi departemen dalam organisasi
 */
@Entity
@Table(name = "departemen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departemen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nama;
    
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    
    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;
    
    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
    }
}
