package com.ids.hhub.repository;

import com.ids.hhub.model.Hackathon;
import com.ids.hhub.model.SupportRequest;
import com.ids.hhub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByHackathonId(Long hackathonId);

    Optional<SupportRequest> findByTeamAndHackathonAndProblemDescription(Team team, Hackathon hackathon, String problemDescription);
}
