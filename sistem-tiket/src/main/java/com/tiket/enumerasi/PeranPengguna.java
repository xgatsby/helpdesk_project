package com.tiket.enumerasi;

/**
 * Enum yang merepresentasikan peran pengguna dalam sistem tiket.
 * Digunakan untuk membedakan hak akses dan fungsi setiap pengguna.
 */
public enum PeranPengguna {
    /**
     * Administrator sistem dengan akses penuh
     */
    ADMIN("Administrator"),
    
    /**
     * Agen yang menangani tiket pelanggan
     */
    AGEN("Agen"),
    
    /**
     * Pelanggan yang membuat tiket
     */
    PELANGGAN("Pelanggan");
    
    private final String nama;
    
    /**
     * Constructor untuk PeranPengguna
     * @param nama Nama tampilan untuk peran pengguna
     */
    PeranPengguna(String nama) {
        this.nama = nama;
    }
    
    /**
     * Mendapatkan nama tampilan dari peran pengguna
     * @return Nama tampilan peran
     */
    public String getNama() {
        return nama;
    }
}
