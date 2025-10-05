package com.bantuan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "departemen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departemen {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nama;

    private String deskripsi;

    @Column(name = "induk_departemen_id")
    private UUID indukDepartemenId;

    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;

    @Column(name = "diperbarui_pada")
    private LocalDateTime diperbaruiPada;

    @OneToMany(mappedBy = "indukDepartemenId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Departemen> subDepartemen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "induk_departemen_id", insertable = false, updatable = false)
    private Departemen indukDepartemen;

    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
        diperbaruiPada = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        diperbaruiPada = LocalDateTime.now();
    }

    public boolean memilikiInduk() {
        return indukDepartemenId != null;
    }

    public boolean merupakanAkar() {
        return indukDepartemenId == null;
    }
}
