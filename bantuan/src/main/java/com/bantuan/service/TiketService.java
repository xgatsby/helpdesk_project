package com.bantuan.service;

import com.bantuan.entity.Tiket;
import com.bantuan.entity.Pengguna;
import com.bantuan.enums.PrioritasTiket;
import com.bantuan.enums.StatusTiket;
import com.bantuan.repository.TiketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TiketService {

    private final TiketRepository tiketRepository;
    private final SLAService slaService;

    @Transactional
    public Tiket createTicket(String subject, String description, String category,
                          PrioritasTiket prioritas, UUID departemenId, UUID dibuatOleh) {
        Tiket tiket = new Tiket();
        tiket.setSubjek(subject);
        tiket.setDeskripsi(description);
        tiket.setKategori(category);
        tiket.setPrioritas(prioritas);
        tiket.setDepartemenId(departemenId);
        tiket.setDibuatOleh(dibuatOleh);
        tiket.setJamSla(slaService.getSlaHoursForPriority(prioritas));
        tiket.setBatasWaktuSla(slaService.calculateSlaDueAt(prioritas));

        return tiketRepository.save(tiket);
    }

    public Tiket getTicket(UUID id) {
        return tiketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));
    }

    public Page<Tiket> getTickets(Pageable pageable, StatusTiket status, PrioritasTiket prioritas, UUID departemenId) {
        if (status != null && prioritas != null && departemenId != null) {
            // Complex query with multiple filters - would need custom query
            return tiketRepository.findAll(pageable);
        } else if (status != null) {
            return tiketRepository.findByStatus(status, pageable);
        } else if (prioritas != null) {
            return tiketRepository.findByPrioritas(prioritas, pageable);
        } else if (departemenId != null) {
            return tiketRepository.findByDepartemenId(departemenId, pageable);
        } else {
            return tiketRepository.findAll(pageable);
        }
    }

    @Transactional
    public Tiket assignTicket(UUID tiketId, UUID agenId) {
        Tiket tiket = tiketRepository.findByIdForUpdate(tiketId)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));

        tiket.setDitugaskanKe(agenId);
        tiket.setStatus(StatusTiket.DITUGASKAN);
        return tiketRepository.save(tiket);
    }

    @Transactional
    public Tiket resolveTicket(UUID tiketId) {
        Tiket tiket = tiketRepository.findByIdForUpdate(tiketId)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));

        tiket.setStatus(StatusTiket.SELESAI);
        tiket.setWaktuSelesai(java.time.LocalDateTime.now());
        return tiketRepository.save(tiket);
    }

    @Transactional
    public Tiket closeTicket(UUID tiketId) {
        Tiket tiket = tiketRepository.findByIdForUpdate(tiketId)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));

        tiket.setStatus(StatusTiket.DITUTUP);
        tiket.setWaktuDitutup(java.time.LocalDateTime.now());
        return tiketRepository.save(tiket);
    }

    public void updateTicketStatus(UUID tiketId, StatusTiket status) {
        Tiket tiket = tiketRepository.findByIdForUpdate(tiketId)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));

        tiket.setStatus(status);
        tiketRepository.save(tiket);
    }
}
