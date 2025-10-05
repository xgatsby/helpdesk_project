package com.bantuan.controller;

import com.bantuan.entity.Tiket;
import com.bantuan.enums.PrioritasTiket;
import com.bantuan.enums.StatusTiket;
import com.bantuan.service.PenugasanService;
import com.bantuan.service.SLAService;
import com.bantuan.service.TiketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tiket")
public class TiketController {

    private final TiketService tiketService;
    private final PenugasanService penugasanService;
    private final SLAService slaService;

    public TiketController(TiketService tiketService, PenugasanService penugasanService, SLAService slaService) {
        this.tiketService = tiketService;
        this.penugasanService = penugasanService;
        this.slaService = slaService;
    }

    @PostMapping
    public ResponseEntity<Tiket> createTicket(@RequestBody TiketRequest request) {
        Tiket tiket = tiketService.createTicket(
            request.getSubjek(),
            request.getDeskripsi(),
            request.getKategori(),
            request.getPrioritas(),
            request.getDepartemenId(),
            request.getDibuatOleh()
        );

        // Auto-assign tiket
        penugasanService.assignTicket(tiket);

        return ResponseEntity.ok(tiket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tiket> getTicket(@PathVariable UUID id) {
        Tiket tiket = tiketService.getTicket(id);
        return ResponseEntity.ok(tiket);
    }

    @GetMapping
    public ResponseEntity<Page<Tiket>> getTickets(Pageable pageable,
                                           @RequestParam(required = false) StatusTiket status,
                                           @RequestParam(required = false) PrioritasTiket prioritas,
                                           @RequestParam(required = false) UUID departemenId) {
        Page<Tiket> tiket = tiketService.getTickets(pageable, status, prioritas, departemenId);
        return ResponseEntity.ok(tiket);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Tiket> assignTicket(@PathVariable UUID id,
                                       @RequestParam UUID agenId) {
        Tiket tiket = tiketService.assignTicket(id, agenId);
        return ResponseEntity.ok(tiket);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Tiket> closeTicket(@PathVariable UUID id) {
        Tiket tiket = tiketService.closeTicket(id);
        return ResponseEntity.ok(tiket);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Tiket> resolveTicket(@PathVariable UUID id) {
        Tiket tiket = tiketService.resolveTicket(id);
        return ResponseEntity.ok(tiket);
    }

    static class TiketRequest {
        private String subjek;
        private String deskripsi;
        private String kategori;
        private PrioritasTiket prioritas;
        private UUID departemenId;
        private UUID dibuatOleh;

        // Getters and setters
        public String getSubjek() { return subjek; }
        public void setSubjek(String subjek) { this.subjek = subjek; }
        public String getDeskripsi() { return deskripsi; }
        public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
        public String getKategori() { return kategori; }
        public void setKategori(String kategori) { this.kategori = kategori; }
        public PrioritasTiket getPrioritas() { return prioritas; }
        public void setPrioritas(PrioritasTiket prioritas) { this.prioritas = prioritas; }
        public UUID getDepartemenId() { return departemenId; }
        public void setDepartemenId(UUID departemenId) { this.departemenId = departemenId; }
        public UUID getDibuatOleh() { return dibuatOleh; }
        public void setDibuatOleh(UUID dibuatOleh) { this.dibuatOleh = dibuatOleh; }
    }
}
