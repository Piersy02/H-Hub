package com.ids.hhub.repository;

import com.ids.hhub.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByTeamId(Long teamId);
}
