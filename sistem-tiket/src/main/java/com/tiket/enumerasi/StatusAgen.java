package com.tiket.enumerasi;

/**
 * Enum yang merepresentasikan status ketersediaan agen.
 * Digunakan untuk menentukan apakah agen dapat menerima tiket baru.
 */
public enum StatusAgen {
    /**
     * Agen tersedia dan dapat menerima tiket baru
     */
    TERSEDIA("Tersedia"),
    
    /**
     * Agen sedang sibuk menangani tiket
     */
    SIBUK("Sibuk"),
    
    /**
     * Agen sedang istirahat
     */
    ISTIRAHAT("Istirahat"),
    
    /**
     * Agen sedang offline
     */
    OFFLINE("Offline");
    
    private final String nama;
    
    /**
     * Constructor untuk StatusAgen
     * @param nama Nama tampilan untuk status agen
     */
    StatusAgen(String nama) {
        this.nama = nama;
    }
    
    /**
     * Mendapatkan nama tampilan dari status agen
     * @return Nama tampilan status
     */
    public String getNama() {
        return nama;
    }
}
