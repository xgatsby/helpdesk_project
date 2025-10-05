package com.bantuan.service;

import com.bantuan.entity.Agen;
import com.bantuan.entity.Tiket;
import com.bantuan.enums.StatusAgen;
import com.bantuan.enums.StrategiPenugasan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenugasanService {

    private final AgenRepository agenRepository;
    private final TiketRepository tiketRepository;

    @Value("${penugasan.strategi:BERDASARKAN_BEBAN_KERJA}")
    private StrategiPenugasan strategiPenugasan;

    @Transactional
    public Agen assignTicket(Tiket tiket) {
        List<Agen> availableAgents = getAvailableAgents(tiket.getDepartemenId());
        if (availableAgents.isEmpty()) {
            log.warn("Tidak ada agen tersedia untuk tiket: {}", tiket.getId());
            return null;
        }

        Agen selectedAgent = selectAgent(availableAgents, tiket);
        if (selectedAgent != null) {
            updateAgentAssignment(selectedAgent, tiket);
        }

        return selectedAgent;
    }

    private List<Agen> getAvailableAgents(UUID departemenId) {
        return switch (strategiPenugasan) {
            case RODA_PUTAR -> agenRepository.findAvailableAgentsByDepartmentAndWorkload(departemenId);
            case BERDASARKAN_BEBAN_KERJA -> agenRepository.findAvailableAgentsByWorkload();
            case BERDASARKAN_DEPARTEMEN -> agenRepository.findAvailableAgentsByDepartment(departemenId, StatusAgen.TERSEDIA);
        };
    }

    private Agen selectAgent(List<Agen> agents, Tiket tiket) {
        return switch (strategiPenugasan) {
            case RODA_PUTAR -> selectByRoundRobin(agents, tiket);
            case BERDASARKAN_BEBAN_KERJA -> selectByWorkload(agents);
            case BERDASARKAN_DEPARTEMEN -> selectByDepartment(agents, tiket);
        };
    }

    private Agen selectByWorkload(List<Agen> agents) {
        return agents.stream()
                .min((a1, a2) -> Double.compare(a1.getWorkloadPercentage(), a2.getWorkloadPercentage()))
                .orElse(null);
    }

    private Agen selectByRoundRobin(List<Agen> agents, Tiket tiket) {
        return agents.stream()
                .min((a1, a2) -> {
                    int comparison = Integer.compare(a1.getJumlahTiketAktif(), a2.getJumlahTiketAktif());
                    if (comparison == 0) {
                        return a1.getId().compareTo(a2.getId());
                    }
                    return comparison;
                })
                .orElse(null);
    }

    private Agen selectByDepartment(List<Agen> agents, Tiket tiket) {
        return agents.stream()
                .filter(agent -> hasRequiredSkills(agent, tiket))
                .findFirst()
                .orElseGet(() -> selectByWorkload(agents));
    }

    private boolean hasRequiredSkills(Agen agent, Tiket tiket) {
        // Implementasi logika skill matching
        return true; // Sementara selalu kembalikan true
    }

    @Transactional
    private void updateAgentAssignment(Agen agent, Tiket tiket) {
        agent.tambahTiketAktif();
        agenRepository.save(agent);

        tiket.setDitugaskanKe(agent.getId());
        tiket.setStatus(com.bantuan.enums.StatusTiket.DITUGASKAN);
        tiketRepository.save(tiket);

        log.info("Tiket {} ditugaskan ke agen {}", tiket.getId(), agent.getId());
    }
}
