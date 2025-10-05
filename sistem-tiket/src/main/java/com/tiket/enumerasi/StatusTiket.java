package com.tiket.enumerasi;

/**
 * Enum yang merepresentasikan status dari tiket dalam sistem.
 * Digunakan untuk melacak progress penanganan tiket.
 */
public enum StatusTiket {
    /**
     * Tiket baru yang belum ditugaskan
     */
    BARU("Baru"),
    
    /**
     * Tiket yang sudah ditugaskan ke agen
     */
    DITUGASKAN("Ditugaskan"),
    
    /**
     * Tiket yang sedang dikerjakan oleh agen
     */
    SEDANG_DIKERJAKAN("Sedang Dikerjakan"),
    
    /**
     * Tiket yang sudah selesai ditangani
     */
    SELESAI("Selesai"),
    
    /**
     * Tiket yang sudah ditutup
     */
    DITUTUP("Ditutup");
    
    private final String nama;
    
    /**
     * Constructor untuk StatusTiket
     * @param nama Nama tampilan untuk status tiket
     */
    StatusTiket(String nama) {
        this.nama = nama;
    }
    
    /**
     * Mendapatkan nama tampilan dari status tiket
     * @return Nama tampilan status
     */
    public String getNama() {
        return nama;
    }
}
