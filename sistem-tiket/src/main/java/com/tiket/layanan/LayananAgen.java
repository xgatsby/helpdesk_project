package com.tiket.layanan;

import com.tiket.entitas.Agen;
import com.tiket.entitas.Tiket;
import com.tiket.enumerasi.StatusAgen;
import com.tiket.repositori.AgenRepository;
import com.tiket.repositori.TiketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service untuk mengelola operasi terkait Agen.
 * Menyediakan logika bisnis untuk manajemen agen dan distribusi beban kerja.
 */
@Service
@RequiredArgsConstructor
public class LayananAgen {
    
    private final AgenRepository agenRepository;
    private final TiketRepository tiketRepository;
    
    /**
     * Mendapatkan daftar agen yang tersedia dalam departemen tertentu
     * 
     * @param departemenId ID departemen
     * @return List berisi agen yang berstatus TERSEDIA di departemen tersebut
     */
    public List<Agen> daftarAgenTersedia(Integer departemenId) {
        return agenRepository.findByStatusAndDepartemenId(
            StatusAgen.TERSEDIA.name(), departemenId
        );
    }
    
    /**
     * Mencari agen dengan beban kerja terendah dalam departemen tertentu
     * Digunakan untuk penugasan otomatis tiket baru
     * 
     * @param departemenId ID departemen
     * @return Optional berisi Agen dengan jumlah tiket aktif paling sedikit, kosong jika tidak ada agen tersedia
     */
    public Optional<Agen> cariAgenDenganBebanTerendah(Integer departemenId) {
        List<Agen> agenTersedia = agenRepository.findByStatusAndDepartemenId(
            StatusAgen.TERSEDIA.name(), departemenId
        );
        
        return agenTersedia.stream()
            .min(Comparator.comparingInt(Agen::getJumlahTiketAktif));
    }
    
    /**
     * Mendapatkan daftar semua agen dalam sistem
     * 
     * @return List berisi semua agen
     */
    public List<Agen> daftarSemuaAgen() {
        return agenRepository.findAll();
    }
    
    /**
     * Mendapatkan daftar tiket yang ditugaskan ke agen tertentu
     * 
     * @param agenId ID agen
     * @return List berisi tiket yang ditugaskan ke agen tersebut
     */
    public List<Tiket> daftarTiketAgen(Integer agenId) {
        return tiketRepository.findByDitugaskanKe(agenId);
    }
}
