-- ========================================
-- Migrasi Database MySQL - Sistem Tiket
-- Versi: 1.0
-- Deskripsi: Inisialisasi skema database
-- ========================================

-- Buat tabel departemen
-- Tabel ini menyimpan informasi departemen dalam organisasi
CREATE TABLE departemen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    dibuat_pada DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_departemen_nama (nama)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Buat tabel pengguna
-- Tabel ini menyimpan informasi pengguna sistem
CREATE TABLE pengguna (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    kata_sandi VARCHAR(255) NOT NULL,
    peran VARCHAR(50) NOT NULL,
    departemen_id INT,
    dibuat_pada DATETIME DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pengguna_email (email),
    INDEX idx_pengguna_peran (peran),
    CONSTRAINT fk_pengguna_departemen FOREIGN KEY (departemen_id) 
        REFERENCES departemen(id) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
    CONSTRAINT chk_peran CHECK (peran IN ('ADMIN', 'AGEN', 'PENGGUNA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Buat tabel agen
-- Tabel ini menyimpan informasi agen support
CREATE TABLE agen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pengguna_id INT NOT NULL UNIQUE,
    departemen_id INT NOT NULL,
    tiket_maksimal INT DEFAULT 5,
    jumlah_tiket_aktif INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'TERSEDIA',
    dibuat_pada DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agen_departemen (departemen_id),
    INDEX idx_agen_status (status),
    INDEX idx_agen_jumlah_aktif (jumlah_tiket_aktif),
    CONSTRAINT fk_agen_pengguna FOREIGN KEY (pengguna_id) 
        REFERENCES pengguna(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_agen_departemen FOREIGN KEY (departemen_id) 
        REFERENCES departemen(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT chk_status_agen CHECK (status IN ('TERSEDIA', 'SIBUK', 'ISTIRAHAT', 'OFFLINE')),
    CONSTRAINT chk_tiket_maksimal CHECK (tiket_maksimal > 0),
    CONSTRAINT chk_jumlah_tiket_aktif CHECK (jumlah_tiket_aktif >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Buat tabel tiket
-- Tabel ini menyimpan informasi tiket support
CREATE TABLE tiket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subjek VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    kategori VARCHAR(100) NOT NULL,
    prioritas VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'BARU',
    departemen_id INT NOT NULL,
    dibuat_oleh INT NOT NULL,
    ditugaskan_ke INT,
    jam_sla INT NOT NULL,
    batas_waktu_sla DATETIME NOT NULL,
    waktu_selesai DATETIME,
    dibuat_pada DATETIME DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tiket_status (status),
    INDEX idx_tiket_prioritas (prioritas),
    INDEX idx_tiket_ditugaskan_ke (ditugaskan_ke),
    INDEX idx_tiket_batas_waktu_sla (batas_waktu_sla),
    INDEX idx_tiket_kategori (kategori),
    INDEX idx_tiket_departemen (departemen_id),
    INDEX idx_tiket_dibuat_pada (dibuat_pada),
    CONSTRAINT fk_tiket_departemen FOREIGN KEY (departemen_id) 
        REFERENCES departemen(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_tiket_dibuat_oleh FOREIGN KEY (dibuat_oleh) 
        REFERENCES pengguna(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_tiket_ditugaskan_ke FOREIGN KEY (ditugaskan_ke) 
        REFERENCES agen(id) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
    CONSTRAINT chk_prioritas CHECK (prioritas IN ('TERENDAH', 'RENDAH', 'SEDANG', 'TINGGI', 'MENDESAK')),
    CONSTRAINT chk_status_tiket CHECK (status IN ('BARU', 'DITUGASKAN', 'SEDANG_DIPROSES', 'DITUNDA', 'SELESAI', 'DITUTUP')),
    CONSTRAINT chk_jam_sla CHECK (jam_sla > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Buat tabel komentar_tiket
-- Tabel ini menyimpan komentar pada tiket
CREATE TABLE komentar_tiket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tiket_id INT NOT NULL,
    penulis_id INT NOT NULL,
    isi TEXT NOT NULL,
    dibuat_pada DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_komentar_tiket (tiket_id),
    INDEX idx_komentar_dibuat_pada (dibuat_pada),
    CONSTRAINT fk_komentar_tiket FOREIGN KEY (tiket_id) 
        REFERENCES tiket(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_komentar_penulis FOREIGN KEY (penulis_id) 
        REFERENCES pengguna(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Data Awal
-- ========================================

-- Insert departemen
INSERT INTO departemen (nama, deskripsi) VALUES
    ('Dukungan Umum', 'Departemen untuk menangani pertanyaan umum pelanggan'),
    ('Dukungan Teknis', 'Departemen untuk menangani masalah teknis dan troubleshooting'),
    ('Penagihan', 'Departemen untuk menangani pertanyaan terkait pembayaran dan faktur');

-- Insert pengguna admin
-- Password: admin123 (hash bcrypt)
INSERT INTO pengguna (nama, email, kata_sandi, peran, departemen_id) VALUES
    ('Administrator', 'admin@sistemtiket.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7tR4YCdX07Ck9ggNGfD2fWccXwVL1xW', 'ADMIN', NULL);

-- Insert pengguna agen
INSERT INTO pengguna (nama, email, kata_sandi, peran, departemen_id) VALUES
    ('Budi Santoso', 'budi.santoso@sistemtiket.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7tR4YCdX07Ck9ggNGfD2fWccXwVL1xW', 'AGEN', 1),
    ('Siti Nurhaliza', 'siti.nurhaliza@sistemtiket.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7tR4YCdX07Ck9ggNGfD2fWccXwVL1xW', 'AGEN', 2);

-- Insert agen
INSERT INTO agen (pengguna_id, departemen_id, tiket_maksimal, jumlah_tiket_aktif, status) VALUES
    (2, 1, 10, 0, 'TERSEDIA'),
    (3, 2, 8, 0, 'TERSEDIA');

-- ========================================
-- Selesai
-- ========================================
