-- Buat tipe enum
CREATE TYPE prioritas_tiket AS ENUM ('TERENDAH', 'RENDAH', 'SEDANG', 'TINGGI', 'MENDESAK');
CREATE TYPE status_tiket AS ENUM ('BARU', 'DITUGASKAN', 'SEDANG_DIPROSES', 'DITUNDA', 'SELESAI', 'DITUTUP');
CREATE TYPE status_agen AS ENUM ('TERSEDIA', 'SIBUK', 'ISTIRAHAT', 'OFFLINE');

-- Buat tabel
CREATE TABLE pengguna (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    peran VARCHAR(50) NOT NULL,
    departemen_id UUID,
    dibuat_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE departemen (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    induk_departemen_id UUID,
    dibuat_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (induk_departemen_id) REFERENCES departemen(id)
);

CREATE TABLE agen (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pengguna_id UUID NOT NULL UNIQUE,
    departemen_id UUID NOT NULL,
    keterampilan TEXT[],
    tiket_maksimal INTEGER DEFAULT 5,
    jumlah_tiket_aktif INTEGER DEFAULT 0,
    status status_agen DEFAULT 'TERSEDIA',
    dibuat_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pengguna_id) REFERENCES pengguna(id) ON DELETE CASCADE,
    FOREIGN KEY (departemen_id) REFERENCES departemen(id)
);

CREATE TABLE tiket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subjek VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    kategori VARCHAR(100) NOT NULL,
    prioritas prioritas_tiket NOT NULL,
    status status_tiket DEFAULT 'BARU',
    departemen_id UUID NOT NULL,
    dibuat_oleh UUID NOT NULL,
    ditugaskan_ke UUID,
    jam_sla INTEGER NOT NULL,
    batas_waktu_sla TIMESTAMP WITH TIME ZONE NOT NULL,
    tingkat_ eskalasi INTEGER DEFAULT 0,
    waktu_selesai TIMESTAMP WITH TIME ZONE,
    waktu_ditutup TIMESTAMP WITH TIME ZONE,
    dibuat_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (departemen_id) REFERENCES departemen(id),
    FOREIGN KEY (dibuat_oleh) REFERENCES pengguna(id),
    FOREIGN KEY (ditugaskan_ke) REFERENCES agen(id)
);

CREATE TABLE komentar_tiket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tiket_id UUID NOT NULL,
    penulis_id UUID NOT NULL,
    isi TEXT NOT NULL,
    bersifat_internal BOOLEAN DEFAULT false,
    dibuat_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tiket_id) REFERENCES tiket(id) ON DELETE CASCADE,
    FOREIGN KEY (penulis_id) REFERENCES pengguna(id)
);

CREATE TABLE lampiran_tiket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tiket_id UUID NOT NULL,
    nama_file VARCHAR(255) NOT NULL,
    ukuran_file BIGINT NOT NULL,
    tipe_file VARCHAR(100) NOT NULL,
    url VARCHAR(500) NOT NULL,
    diunggah_oleh UUID NOT NULL,
    dibuat_pada TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tiket_id) REFERENCES tiket(id) ON DELETE CASCADE,
    FOREIGN KEY (diunggah_oleh) REFERENCES pengguna(id)
);

-- Buat indeks
CREATE INDEX idx_tiket_status ON tiket(status);
CREATE INDEX idx_tiket_prioritas ON tiket(prioritas);
CREATE INDEX idx_tiket_ditugaskan_ke ON tiket(ditugaskan_ke);
CREATE INDEX idx_tiket_batas_waktu_sla ON tiket(batas_waktu_sla);
CREATE INDEX idx_tiket_dibuat_pada ON tiket(dibuat_pada);
CREATE INDEX idx_tiket_kategori ON tiket(kategori);
CREATE INDEX idx_tiket_departemen ON tiket(departemen_id);

CREATE INDEX idx_agen_departemen ON agen(departemen_id);
CREATE INDEX idx_agen_status ON agen(status);
CREATE INDEX idx_agen_jumlah_aktif ON agen(jumlah_tiket_aktif);

CREATE INDEX idx_departemen_induk ON departemen(induk_departemen_id);

CREATE INDEX idx_komentar_tiket ON komentar_tiket(tiket_id);
CREATE INDEX idx_komentar_dibuat_pada ON komentar_tiket(dibuat_pada);

-- Masukkan data awal
INSERT INTO departemen (nama, deskripsi) VALUES
    ('Dukungan', 'Departemen Dukungan Pelanggan'),
    ('Teknis', 'Departemen Dukungan Teknis'),
    ('Penagihan', 'Departemen Penagihan');

-- Buat pengguna admin
INSERT INTO pengguna (nama, email, password_hash, peran) VALUES
    ('Admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/92p1fL4fEa3Ro9llC', 'ADMIN');
