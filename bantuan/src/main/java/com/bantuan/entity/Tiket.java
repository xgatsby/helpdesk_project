package com.bantuan.entity;

import com.bantuan.enums.PrioritasTiket;
import com.bantuan.enums.StatusTiket;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tiket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tiket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String subjek;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String deskripsi;

    @Column(nullable = false)
    private String kategori;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioritasTiket prioritas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTiket status = StatusTiket.BARU;

    @Column(name = "departemen_id", nullable = false)
    private UUID departemenId;

    @Column(name = "dibuat_oleh", nullable = false)
    private UUID dibuatOleh;

    @Column(name = "ditugaskan_ke")
    private UUID ditugaskanKe;

    @Column(name = "jam_sla", nullable = false)
    private Integer jamSla;

    @Column(name = "batas_waktu_sla", nullable = false)
    private LocalDateTime batasWaktuSla;

    @Column(name = "tingkat_ eskalasi", nullable = false)
    private Integer tingkatEskalasi = 0;

    @Column(name = "waktu_selesai")
    private LocalDateTime waktuSelesai;

    @Column(name = "waktu_ditutup")
    private LocalDateTime waktuDitutup;

    @Column(name = "dibuat_pada")
    private LocalDateTime dibuatPada;

    @Column(name = "diperbarui_pada")
    private LocalDateTime diperbaruiPada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departemen_id", insertable = false, updatable = false)
    private Departemen departemen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dibuat_oleh", insertable = false, updatable = false)
    private Pengguna dibuatOlehUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ditugaskan_ke", insertable = false, updatable = false)
    private Agen ditugaskanKeAgen;

    @PrePersist
    protected void onCreate() {
        dibuatPada = LocalDateTime.now();
        diperbaruiPada = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        diperbaruiPada = LocalDateTime.now();
    }

    public boolean terlambat() {
        return LocalDateTime.now().isAfter(batasWaktuSla);
    }

    public boolean selesai() {
        return status == StatusTiket.SELESAI;
    }

    public boolean ditutup() {
        return status == StatusTiket.DITUTUP;
    }

    public double getProgressPercentage() {
        if (waktuSelesai != null) {
            return 100.0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(batasWaktuSla)) {
            return 100.0;
        }
        long totalMinutes = java.time.Duration.between(dibuatPada, batasWaktuSla).toMinutes();
        long elapsedMinutes = java.time.Duration.between(dibuatPada, now).toMinutes();
        return Math.min(100.0, (double) elapsedMinutes / totalMinutes * 100);
    }
}
