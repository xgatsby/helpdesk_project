package com.tiket.layanan;

import com.tiket.entitas.Pengguna;
import com.tiket.repositori.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service untuk mengelola operasi terkait Pengguna.
 * Menyediakan logika bisnis untuk registrasi, pencarian, dan manajemen pengguna.
 */
@Service
@RequiredArgsConstructor
public class LayananPengguna {
    
    private final PenggunaRepository penggunaRepository;
    
    /**
     * Mendaftarkan pengguna baru ke dalam sistem
     * 
     * PERINGATAN: Kata sandi disimpan sebagai plain text untuk kesederhanaan beginner.
     * Di aplikasi production, WAJIB menggunakan hashing (BCrypt, Argon2, dll) untuk keamanan!
     * 
     * @param nama Nama lengkap pengguna
     * @param email Alamat email pengguna (harus unik)
     * @param kataSandi Kata sandi pengguna
     * @param peran Peran pengguna dalam sistem (ADMIN, AGEN, PELANGGAN)
     * @param departemenId ID departemen pengguna (opsional untuk beberapa peran)
     * @return Pengguna yang baru didaftarkan
     * @throws RuntimeException jika email sudah terdaftar
     */
    @Transactional
    public Pengguna daftarPengguna(String nama, String email, String kataSandi, String peran, Integer departemenId) {
        Optional<Pengguna> penggunaExisting = penggunaRepository.findByEmail(email);
        if (penggunaExisting.isPresent()) {
            throw new RuntimeException("Email sudah terdaftar: " + email);
        }
        
        Pengguna pengguna = new Pengguna();
        pengguna.setNama(nama);
        pengguna.setEmail(email);
        pengguna.setKataSandi(kataSandi);
        pengguna.setPeran(peran);
        pengguna.setDepartemenId(departemenId);
        
        return penggunaRepository.save(pengguna);
    }
    
    /**
     * Mencari pengguna berdasarkan alamat email
     * 
     * @param email Alamat email yang dicari
     * @return Optional berisi Pengguna jika ditemukan, kosong jika tidak
     */
    public Optional<Pengguna> cariByEmail(String email) {
        return penggunaRepository.findByEmail(email);
    }
    
    /**
     * Mendapatkan daftar semua pengguna dalam sistem
     * 
     * @return List berisi semua pengguna
     */
    public List<Pengguna> daftarSemuaPengguna() {
        return penggunaRepository.findAll();
    }
    
    /**
     * Mencari pengguna berdasarkan ID
     * 
     * @param id ID pengguna yang dicari
     * @return Optional berisi Pengguna jika ditemukan, kosong jika tidak
     */
    public Optional<Pengguna> cariById(Integer id) {
        return penggunaRepository.findById(id);
    }
}
