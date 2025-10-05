package com.tiket.enumerasi;

/**
 * Enum yang merepresentasikan tingkat prioritas tiket.
 * Setiap prioritas memiliki SLA (Service Level Agreement) dalam satuan jam.
 */
public enum PrioritasTiket {
    /**
     * Prioritas rendah dengan SLA 48 jam
     */
    RENDAH("Rendah", 48),
    
    /**
     * Prioritas sedang dengan SLA 24 jam
     */
    SEDANG("Sedang", 24),
    
    /**
     * Prioritas tinggi dengan SLA 8 jam
     */
    TINGGI("Tinggi", 8),
    
    /**
     * Prioritas mendesak dengan SLA 4 jam
     */
    MENDESAK("Mendesak", 4);
    
    private final String nama;
    private final int jamSla;
    
    /**
     * Constructor untuk PrioritasTiket
     * @param nama Nama tampilan untuk prioritas tiket
     * @param jamSla Waktu SLA dalam jam
     */
    PrioritasTiket(String nama, int jamSla) {
        this.nama = nama;
        this.jamSla = jamSla;
    }
    
    /**
     * Mendapatkan nama tampilan dari prioritas tiket
     * @return Nama tampilan prioritas
     */
    public String getNama() {
        return nama;
    }
    
    /**
     * Mendapatkan waktu SLA dalam jam untuk prioritas ini
     * @return Jumlah jam SLA
     */
    public int getJamSla() {
        return jamSla;
    }
}
