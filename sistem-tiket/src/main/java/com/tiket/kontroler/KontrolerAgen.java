package com.tiket.kontroler;

import com.tiket.entitas.Agen;
import com.tiket.entitas.Tiket;
import com.tiket.layanan.LayananAgen;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller untuk mengelola operasi Agen
 * Menyediakan endpoint untuk pencarian agen dan manajemen beban kerja agen
 */
@RestController
@RequestMapping("/api/agen")
@RequiredArgsConstructor
public class KontrolerAgen {
    
    private final LayananAgen layananAgen;
    
    /**
     * Mendapatkan daftar semua agen dalam sistem
     * 
     * @return ResponseEntity berisi List semua agen
     */
    @GetMapping
    public ResponseEntity<?> daftarSemuaAgen() {
        try {
            List<Agen> agenList = layananAgen.daftarSemuaAgen();
            return ResponseEntity.ok(agenList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan daftar tiket yang ditugaskan ke agen tertentu
     * 
     * @param id ID agen
     * @return ResponseEntity berisi List tiket yang ditugaskan ke agen tersebut
     */
    @GetMapping("/{id}/tiket")
    public ResponseEntity<?> daftarTiketAgen(@PathVariable Integer id) {
        try {
            List<Tiket> tiketList = layananAgen.daftarTiketAgen(id);
            return ResponseEntity.ok(tiketList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan daftar agen yang tersedia dalam departemen tertentu
     * 
     * @param departemenId ID departemen (query parameter)
     * @return ResponseEntity berisi List agen yang tersedia di departemen tersebut
     */
    @GetMapping("/tersedia")
    public ResponseEntity<?> daftarAgenTersedia(@RequestParam Integer departemenId) {
        try {
            List<Agen> agenList = layananAgen.daftarAgenTersedia(departemenId);
            return ResponseEntity.ok(agenList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
