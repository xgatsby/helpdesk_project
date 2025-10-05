package com.bantuan.entity;

import com.bantuan.enums.StatusAgen;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agen {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pengguna_id", nullable = false, unique = true)
    private UUID penggunaId;

    @Column(name = "departemen_id", nullable = false)
    private UUID departemenId;

    @ElementCollection
    private String[] keterampilan;

    @Column(name = "tiket_maksimal", nullable = false)
    private Integer tiketMaksimal = 5;

    @Column(name = "jumlah_tiket_aktif", nullable = false)
    private Integer jumlahTiketAktif = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgen status = StatusAgen.TERSEDIA;

    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;

    @Column(name = "diperbarui_pada")
    private LocalDateTime diperbaruiPada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departemen_id", insertable = false, updatable = false)
    private Departemen departemen;

    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
        diperbaruiPada = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        diperbaruiPada = LocalDateTime.now();
    }

    public double getWorkloadPercentage() {
        return tiketMaksimal > 0 ? (double) jumlahTiketAktif / tiketMaksimal * 100 : 0;
    }

    public boolean penuh() {
        return jumlahTiketAktif >= tiketMaksimal;
    }

    public boolean tersedia() {
        return status == StatusAgen.TERSEDIA && !penuh();
    }

    public void tambahTiketAktif() {
        this.jumlahTiketAktif++;
    }

    public void kurangiTiketAktif() {
        this.jumlahTiketAktif = Math.max(0, this.jumlahTiketAktif - 1);
    }
}
