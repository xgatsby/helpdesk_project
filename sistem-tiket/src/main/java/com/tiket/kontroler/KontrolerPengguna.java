package com.tiket.kontroler;

import com.tiket.entitas.Pengguna;
import com.tiket.layanan.LayananPengguna;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller untuk mengelola operasi Pengguna
 * Menyediakan endpoint untuk registrasi, pencarian, dan manajemen pengguna
 */
@RestController
@RequestMapping("/api/pengguna")
@RequiredArgsConstructor
public class KontrolerPengguna {
    
    private final LayananPengguna layananPengguna;
    
    /**
     * Mendaftarkan pengguna baru ke dalam sistem
     * 
     * @param request Map berisi data pengguna: nama, email, kataSandi, peran, departemenId
     * @return ResponseEntity berisi Pengguna yang berhasil didaftarkan atau pesan error
     */
    @PostMapping("/daftar")
    public ResponseEntity<?> daftarPengguna(@RequestBody Map<String, Object> request) {
        try {
            String nama = (String) request.get("nama");
            String email = (String) request.get("email");
            String kataSandi = (String) request.get("kataSandi");
            String peran = (String) request.get("peran");
            Integer departemenId = request.get("departemenId") != null ? 
                Integer.valueOf(request.get("departemenId").toString()) : null;
            
            Pengguna pengguna = layananPengguna.daftarPengguna(nama, email, kataSandi, peran, departemenId);
            return ResponseEntity.status(HttpStatus.CREATED).body(pengguna);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan pengguna berdasarkan ID
     * 
     * @param id ID pengguna yang dicari
     * @return ResponseEntity berisi Pengguna jika ditemukan atau status 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPenggunaById(@PathVariable Integer id) {
        try {
            Optional<Pengguna> pengguna = layananPengguna.cariById(id);
            if (pengguna.isPresent()) {
                return ResponseEntity.ok(pengguna.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Pengguna tidak ditemukan dengan ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mendapatkan daftar semua pengguna dalam sistem
     * 
     * @return ResponseEntity berisi List semua pengguna
     */
    @GetMapping
    public ResponseEntity<?> daftarSemuaPengguna() {
        try {
            List<Pengguna> penggunaList = layananPengguna.daftarSemuaPengguna();
            return ResponseEntity.ok(penggunaList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mencari pengguna berdasarkan email
     * 
     * @param email Alamat email yang dicari (query parameter)
     * @return ResponseEntity berisi Pengguna jika ditemukan atau status 404
     */
    @GetMapping("/cari")
    public ResponseEntity<?> cariPenggunaByEmail(@RequestParam String email) {
        try {
            Optional<Pengguna> pengguna = layananPengguna.cariByEmail(email);
            if (pengguna.isPresent()) {
                return ResponseEntity.ok(pengguna.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Pengguna tidak ditemukan dengan email: " + email));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
