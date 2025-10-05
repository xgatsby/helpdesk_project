package com.bantuan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pengguna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pengguna {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String peran;

    @Column(name = "departemen_id")
    private UUID departemenId;

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
