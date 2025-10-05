# Sistem Bantuan Pelanggan

Sistem manajemen tiket layanan pelanggan komprehensif yang dibangun dengan Spring Boot 3.2.x, PostgreSQL, dan Redis.

## Fitur

- **Penugasan Otomatis**: Algoritma penugasan berbasis beban kerja, putaran, dan departemen
- **Manajemen SLA**: Perhitungan dan pemantauan SLA otomatis dengan eskalasi
- **Pemantauan Real-time**: Pemantauan 5-menit dengan ambang peringatan/kritis
- **Penanganan Permintaan Bersamaan**: Penguncian pesimistis untuk pencegahan kondisi balapan
- **Caching**: Strategi cache-aside berbasis Redis
- **Notifikasi Asinkron**: RabbitMQ dengan dukungan antrian pesan mati

## Arsitektur

### Komponen Utama

- **Presentasi**: REST API Controller (Spring MVC)
- **Aplikasi**: Layanan Bisnis dengan Manajemen Transaksi
- **Domain**: Entitas dan Logika Bisnis
- **Infrastruktur**: PostgreSQL, Redis, RabbitMQ

### Skema Database

- `tiket` - Pelacakan tiket utama
- `agen` - Manajemen agen dengan pelacakan beban kerja
- `pengguna` - Otentikasi dan peran pengguna
- `departemen` - Struktur departemen hirarkis
- `komentar_tiket` - Komentar internal dan eksternal

## Persiapan

1. **Prasyarat**
   - Java 17
   - PostgreSQL 15
   - Redis 7.x (opsional)
   - RabbitMQ 3.12 (opsional)

2. **Konfigurasi**
   - Perbarui `application.yml` dengan kredensial database Anda
   - Konfigurasi koneksi Redis dan RabbitMQ jika digunakan

3. **Jalankan Migrasi**
   ```bash
   mvn flyway:migrate
   ```

4. **Jalankan Aplikasi**
   ```bash
   mvn spring-boot:run
   ```

## Endpoint API

### Otentikasi
- `POST /auth/register` - Pendaftaran pengguna
- `POST /auth/login` - Otentikasi JWT

### Tiket
- `POST /tiket` - Buat tiket dengan penugasan otomatis
- `GET /tiket/{id}` - Dapatkan detail tiket
- `GET /tiket` - Daftar dengan filter dan paginasi
- `POST /tiket/{id}/assign` - Penugasan manual
- `POST /tiket/{id}/close` - Tutup tiket

### Agen & Departemen
- `GET /agen` - Daftar agen
- `GET /agen/{id}/tiket` - Tiket agen
- `GET /departemen` - Struktur pohon departemen

## Strategi Penugasan

1. **BERDASARKAN_BEBAN_KERJA** (default) - Tugaskan ke agen dengan persentase beban kerja terendah
2. **RODA_PUTAR** - Putar melalui agen yang tersedia
3. **BERDASARKAN_DEPARTEMEN** - Penugasan berbasis keterampilan dalam departemen

## Konfigurasi SLA

- MENDESAK: 4 jam
- TINGGI: 8 jam
- SEDANG: 24 jam
- RENDAH: 48 jam
- TERENDAH: 72 jam

## Pemantauan

- Ambang peringatan: 75% dari waktu SLA
- Ambang kritis: 90% dari waktu SLA
- Terlambat: >100% dari waktu SLA

## Pengujian

```bash
mvn test
```

Termasuk pengujian unit dan integrasi komprehensif dengan TestContainers.
