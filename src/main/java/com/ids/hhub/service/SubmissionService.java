package com.ids.hhub.service;

import com.ids.hhub.model.*;
import com.ids.hhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepo;
    @Autowired private TeamRepository teamRepo;
    @Autowired private UserRepository userRepo;

    @Transactional
    public Submission submitProject(Long teamId, String url, String desc, String requesterEmail) {
        Team team = teamRepo.findById(teamId).orElseThrow();
        User requester = userRepo.findByEmail(requesterEmail).orElseThrow();

        if (!team.getMembers().contains(requester)) {
            throw new SecurityException("Devi essere membro del team per sottomettere!");
        }

        team.getHackathon().getCurrentStateObject().submitProject(team.getHackathon(), team);

        // CONTROLLO SCADENZA TEMPORALE
        Hackathon h = team.getHackathon();
        if (LocalDateTime.now().isAfter(h.getEndDate())) {
            throw new RuntimeException("Tempo scaduto! La deadline per la consegna è passata.");
        }

        Submission submission = team.getSubmission();
        if (submission == null) {
            submission = new Submission(url, desc, team);
        } else {
            submission.setProjectUrl(url);
            submission.setDescription(desc);
            submission.setSubmissionDate(LocalDateTime.now());
        }

        return submissionRepo.save(submission);
    }
}
