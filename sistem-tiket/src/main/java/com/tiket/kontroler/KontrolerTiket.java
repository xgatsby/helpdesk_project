package com.tiket.kontroler;

import com.tiket.entitas.KomentarTiket;
import com.tiket.entitas.Tiket;
import com.tiket.layanan.LayananTiket;
import com.tiket.repositori.KomentarTiketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller untuk mengelola operasi Tiket
 * Menyediakan endpoint untuk pembuatan tiket, penugasan otomatis, dan manajemen tiket
 */
@RestController
@RequestMapping("/api/tiket")
@RequiredArgsConstructor
public class KontrolerTiket {
    
    private final LayananTiket layananTiket;
    private final KomentarTiketRepository komentarTiketRepository;
    
    /**
     * Membuat tiket baru dengan penugasan otomatis ke agen
     * 
     * @param request Map berisi data tiket: subjek, deskripsi, kategori, prioritas, departemenId, dibuatOleh
     * @return ResponseEntity berisi Tiket yang berhasil dibuat atau pesan error
     */
    @PostMapping
    public ResponseEntity<?> buatTiket(@RequestBody Map<String, Object> request) {
        try {
            String subjek = (String) request.get("subjek");
            String deskripsi = (String) request.get("deskripsi");
            String kategori = (String) request.get("kategori");
            String prioritas = (String) request.get("prioritas");
            Integer departemenId = Integer.valueOf(request.get("departemenId").toString());
            Integer dibuatOleh = Integer.valueOf(request.get("dibuatOleh").toString());
            
            Tiket tiket = layananTiket.buatTiket(subjek, deskripsi, kategori, prioritas, departemenId, dibuatOleh);
            return ResponseEntity.status(HttpStatus.CREATED).body(tiket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan tiket berdasarkan ID
     * 
     * @param id ID tiket yang dicari
     * @return ResponseEntity berisi Tiket jika ditemukan atau status 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTiketById(@PathVariable Integer id) {
        try {
            Optional<Tiket> tiket = layananTiket.cariTiketById(id);
            if (tiket.isPresent()) {
                return ResponseEntity.ok(tiket.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tiket tidak ditemukan dengan ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan daftar tiket
     * Jika parameter status disediakan, filter berdasarkan status
     * Jika tidak, tampilkan semua tiket
     * 
     * @param status Status tiket (opsional, query parameter)
     * @return ResponseEntity berisi List tiket
     */
    @GetMapping
    public ResponseEntity<?> daftarTiket(@RequestParam(required = false) String status) {
        try {
            List<Tiket> tiketList;
            if (status != null && !status.isEmpty()) {
                tiketList = layananTiket.daftarTiketByStatus(status);
            } else {
                tiketList = layananTiket.daftarSemuaTiket();
            }
            return ResponseEntity.ok(tiketList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Menutup tiket yang sudah selesai ditangani
     * 
     * @param id ID tiket yang akan ditutup
     * @return ResponseEntity berisi Tiket yang telah ditutup atau pesan error
     */
    @PutMapping("/{id}/tutup")
    public ResponseEntity<?> tutupTiket(@PathVariable Integer id) {
        try {
            Tiket tiket = layananTiket.tutupTiket(id);
            return ResponseEntity.ok(tiket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Menambahkan komentar pada tiket
     * 
     * @param id ID tiket yang akan diberi komentar
     * @param request Map berisi data komentar: penulisId, isi
     * @return ResponseEntity berisi KomentarTiket yang berhasil ditambahkan atau pesan error
     */
    @PostMapping("/{id}/komentar")
    public ResponseEntity<?> tambahKomentar(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            Integer penulisId = Integer.valueOf(request.get("penulisId").toString());
            String isi = (String) request.get("isi");
            
            KomentarTiket komentar = layananTiket.tambahKomentar(id, penulisId, isi);
            return ResponseEntity.status(HttpStatus.CREATED).body(komentar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan daftar komentar dari tiket tertentu
     * 
     * @param id ID tiket
     * @return ResponseEntity berisi List komentar dari tiket tersebut
     */
    @GetMapping("/{id}/komentar")
    public ResponseEntity<?> daftarKomentarTiket(@PathVariable Integer id) {
        try {
            List<KomentarTiket> komentarList = komentarTiketRepository.findByTiketIdOrderByDibuatPadaDesc(id);
            return ResponseEntity.ok(komentarList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
