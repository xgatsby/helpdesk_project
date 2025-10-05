package com.tiket.layanan;

import com.tiket.entitas.Agen;
import com.tiket.entitas.KomentarTiket;
import com.tiket.entitas.Tiket;
import com.tiket.enumerasi.PrioritasTiket;
import com.tiket.enumerasi.StatusAgen;
import com.tiket.enumerasi.StatusTiket;
import com.tiket.repositori.AgenRepository;
import com.tiket.repositori.KomentarTiketRepository;
import com.tiket.repositori.TiketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service untuk mengelola operasi terkait Tiket.
 * Menyediakan logika bisnis untuk pembuatan tiket, penugasan otomatis, dan manajemen tiket.
 */
@Service
@RequiredArgsConstructor
public class LayananTiket {
    
    private final TiketRepository tiketRepository;
    private final AgenRepository agenRepository;
    private final KomentarTiketRepository komentarTiketRepository;
    
    /**
     * Membuat tiket baru dengan penugasan otomatis ke agen
     * 
     * @param subjek Judul/subjek tiket
     * @param deskripsi Deskripsi lengkap masalah
     * @param kategori Kategori tiket
     * @param prioritas Prioritas tiket (RENDAH, SEDANG, TINGGI, MENDESAK)
     * @param departemenId ID departemen yang menangani tiket
     * @param dibuatOleh ID pengguna pembuat tiket
     * @return Tiket yang baru dibuat
     * @throws RuntimeException jika prioritas tidak valid
     */
    @Transactional
    public Tiket buatTiket(String subjek, String deskripsi, String kategori, String prioritas, 
                           Integer departemenId, Integer dibuatOleh) {
        PrioritasTiket prioritasTiket;
        try {
            prioritasTiket = PrioritasTiket.valueOf(prioritas.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Prioritas tidak valid: " + prioritas);
        }
        
        int jamSla = prioritasTiket.getJamSla();
        LocalDateTime batasWaktuSla = LocalDateTime.now().plusHours(jamSla);
        
        Tiket tiket = new Tiket();
        tiket.setSubjek(subjek);
        tiket.setDeskripsi(deskripsi);
        tiket.setKategori(kategori);
        tiket.setPrioritas(prioritas.toUpperCase());
        tiket.setStatus(StatusTiket.BARU.name());
        tiket.setDepartemenId(departemenId);
        tiket.setDibuatOleh(dibuatOleh);
        tiket.setJamSla(jamSla);
        tiket.setBatasWaktuSla(batasWaktuSla);
        
        List<Agen> agenTersedia = agenRepository.findByStatusAndDepartemenId(
            StatusAgen.TERSEDIA.name(), departemenId
        );
        
        if (!agenTersedia.isEmpty()) {
            Agen agenTerpilih = agenTersedia.stream()
                .min(Comparator.comparingInt(Agen::getJumlahTiketAktif))
                .orElseThrow(() -> new RuntimeException("Tidak dapat menemukan agen"));
            
            tiket.setDitugaskanKe(agenTerpilih.getId());
            tiket.setStatus(StatusTiket.DITUGASKAN.name());
            
            agenTerpilih.setJumlahTiketAktif(agenTerpilih.getJumlahTiketAktif() + 1);
            agenRepository.save(agenTerpilih);
        }
        
        return tiketRepository.save(tiket);
    }
    
    /**
     * Mencari tiket berdasarkan ID
     * 
     * @param id ID tiket yang dicari
     * @return Optional berisi Tiket jika ditemukan, kosong jika tidak
     */
    public Optional<Tiket> cariTiketById(Integer id) {
        return tiketRepository.findById(id);
    }
    
    /**
     * Mendapatkan daftar semua tiket dalam sistem
     * 
     * @return List berisi semua tiket
     */
    public List<Tiket> daftarSemuaTiket() {
        return tiketRepository.findAll();
    }
    
    /**
     * Mencari tiket berdasarkan status tertentu
     * 
     * @param status Status tiket (BARU, DITUGASKAN, SEDANG_DIKERJAKAN, SELESAI, DITUTUP)
     * @return List berisi tiket dengan status tersebut
     */
    public List<Tiket> daftarTiketByStatus(String status) {
        return tiketRepository.findByStatus(status);
    }
    
    /**
     * Menutup tiket yang sudah selesai ditangani
     * 
     * @param id ID tiket yang akan ditutup
     * @return Tiket yang telah ditutup
     * @throws RuntimeException jika tiket tidak ditemukan
     */
    @Transactional
    public Tiket tutupTiket(Integer id) {
        Tiket tiket = tiketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan dengan ID: " + id));
        
        tiket.setStatus(StatusTiket.DITUTUP.name());
        tiket.setWaktuSelesai(LocalDateTime.now());
        
        if (tiket.getDitugaskanKe() != null) {
            Optional<Agen> agenOpt = agenRepository.findById(tiket.getDitugaskanKe());
            if (agenOpt.isPresent()) {
                Agen agen = agenOpt.get();
                agen.setJumlahTiketAktif(Math.max(0, agen.getJumlahTiketAktif() - 1));
                agenRepository.save(agen);
            }
        }
        
        return tiketRepository.save(tiket);
    }
    
    /**
     * Menambahkan komentar/balasan pada tiket
     * 
     * @param tiketId ID tiket yang akan diberi komentar
     * @param penulisId ID pengguna yang menulis komentar
     * @param isi Isi komentar
     * @return KomentarTiket yang baru dibuat
     * @throws RuntimeException jika tiket tidak ditemukan
     */
    @Transactional
    public KomentarTiket tambahKomentar(Integer tiketId, Integer penulisId, String isi) {
        tiketRepository.findById(tiketId)
            .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan dengan ID: " + tiketId));
        
        KomentarTiket komentar = new KomentarTiket();
        komentar.setTiketId(tiketId);
        komentar.setPenulisId(penulisId);
        komentar.setIsi(isi);
        
        return komentarTiketRepository.save(komentar);
    }
}
