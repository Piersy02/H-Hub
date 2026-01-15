package com.ids.hhub.controller;


import com.ids.hhub.dto.EvaluationDto;
import com.ids.hhub.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired private EvaluationService evaluationService;

    @PostMapping("/submission/{submissionId}")
    public ResponseEntity<String> evaluate(
            @PathVariable Long submissionId,
            @RequestBody EvaluationDto dto,
            Authentication auth
    ) {
        // Il service controlla:
        // Se sei GIUDICE per quell'hackathon
        // Se l'Hackathon è in stato EVALUATION (State Pattern)
        evaluationService.evaluateSubmission(
                submissionId,
                dto.getScore(),
                dto.getComment(),
                auth.getName()
        );

        return ResponseEntity.ok("Valutazione salvata con successo!");
    }
}
