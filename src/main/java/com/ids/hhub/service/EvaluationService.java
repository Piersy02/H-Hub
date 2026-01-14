package com.ids.hhub.service;

import com.ids.hhub.model.Evaluation;
import com.ids.hhub.model.Hackathon;
import com.ids.hhub.model.enums.StaffRole;
import com.ids.hhub.model.Submission;
import com.ids.hhub.model.User;
import com.ids.hhub.repository.EvaluationRepository;
import com.ids.hhub.repository.StaffAssignmentRepository;
import com.ids.hhub.repository.SubmissionRepository;
import com.ids.hhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluationService {

    @Autowired private UserRepository userRepo;
    @Autowired private SubmissionRepository subRepo;
    @Autowired private StaffAssignmentRepository staffRepo;
    @Autowired private EvaluationRepository evalRepo;

    @Transactional
    public void evaluateSubmission(Long submissionId, int score, String comment, String judgeEmail) {
        User judge = userRepo.findByEmail(judgeEmail)
                .orElseThrow(() -> new RuntimeException("Giudice non trovato"));

        Submission sub = subRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Sottomissione non trovata"));

        Hackathon hackathon = sub.getTeam().getHackathon();

        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("Il voto deve essere compreso tra 0 e 10.");
        }

        hackathon.getCurrentStateObject().evaluateProject(hackathon);

        boolean isJudge = staffRepo.existsByUserIdAndHackathonIdAndRole(
                judge.getId(), hackathon.getId(), StaffRole.JUDGE);

        if (!isJudge) {
            throw new SecurityException("Non sei un giudice per questo Hackathon!");
        }

        boolean alreadyVoted = evalRepo.existsBySubmissionAndJudge(sub, judge);
        if (alreadyVoted) {
            throw new RuntimeException("Hai già valutato questo progetto!");
        }

        Evaluation eval = new Evaluation();
        eval.setScore(score);
        eval.setComment(comment);
        eval.setSubmission(sub);
        eval.setJudge(judge);

        evalRepo.save(eval);
    }

}