package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.dto.DoctorDTO;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.projection.DoctorMongoProjection;
import it.unipi.healthhub.projection.DoctorNeo4jProjection;
import it.unipi.healthhub.service.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchAPI {
    @Autowired
    private DoctorService doctorService;

    @GetMapping("/doctors")
    public List<DoctorDTO> searchDoctors(
            @RequestParam String query, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        String patientId = (session != null) ? (String) session.getAttribute("patientId") : null;

        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }

        // 1) prendi da Mongo
        List<DoctorMongoProjection> mongoList = doctorService.searchDoctorsMongo(query);

        // 2) se non c'è patientId, restituisci subito i top‑10 di mongo
        if (patientId == null) {
            return toTop10Dto(mongoList);
        }

        // 3) altrimenti prendi anche da Neo4j
        List<DoctorNeo4jProjection> neo4jList = doctorService.searchDoctorsNeo4j(patientId, query);

        if (neo4jList.isEmpty()) {
            return toTop10Dto(mongoList);
        }
        if (mongoList.isEmpty()) {
            return Collections.emptyList();
        }

        // 4) mappa id→score da Neo4j
        Map<String, Long> neoScores = neo4jList.stream()
                .collect(Collectors.toMap(
                        d -> d.getDoctor().getId(),
                        DoctorNeo4jProjection::getScore
                ));

        // 5) somma i punteggi, ordina e limita a 10, poi mappa in DTO
        return mongoList.stream()
                .peek(dp -> dp.setScore(
                        dp.getScore() + neoScores.getOrDefault(dp.getDoctor().getId(), 6L)
                ))
                .sorted(Comparator.comparingLong(DoctorMongoProjection::getScore).reversed())
                .limit(10)
                .map(dp -> toDto(dp.getDoctor()))
                .collect(Collectors.toList());
    }

    private DoctorDTO toDto(Doctor d) {
        return new DoctorDTO(d);
    }

    /**
     * Ordina per score e prende i primi 10 doctor, poi DTO.
     */
    private List<DoctorDTO> toTop10Dto(List<DoctorMongoProjection> list) {
        return list.stream()
                .sorted(Comparator.comparingLong(DoctorMongoProjection::getScore).reversed())
                .limit(10)
                .map(dp -> toDto(dp.getDoctor()))
                .collect(Collectors.toList());
    }
}