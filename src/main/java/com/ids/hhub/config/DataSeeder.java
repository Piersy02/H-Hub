package com.ids.hhub.config;
import com.ids.hhub.model.*;
import com.ids.hhub.model.enums.*;
import com.ids.hhub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
@Configuration
public class DataSeeder {
    @Bean
    @Transactional
    CommandLineRunner loadData(
            UserRepository userRepo,
            HackathonRepository hackathonRepo,
            TeamRepository teamRepo,
            StaffAssignmentRepository staffRepo,
            SubmissionRepository submissionRepo,
            EvaluationRepository evaluationRepo,
            SupportRequestRepository supportRepo,
            ViolationReportRepository reportRepo,
            TeamInvitationRepository invitationRepo, // Needed for invites
            PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("--- STARING COMPREHENSIVE DATA SEEDING ---");
            // =================================================================================
            // 1. ALL USERS (Platform Actors)
            // =================================================================================

            // --- Global Admin ---
            User admin = createUser(userRepo, passwordEncoder, "Super", "Admin", "admin@hackhub.com", "password", PlatformRole.ADMIN);
            // --- Event Creators (Organizers) ---
            User creator1 = createUser(userRepo, passwordEncoder, "Organizzatore", "Uno", "creator1@hackhub.com", "password", PlatformRole.EVENT_CREATOR);
            User creator2 = createUser(userRepo, passwordEncoder, "Organizzatore", "Due", "creator2@hackhub.com", "password", PlatformRole.EVENT_CREATOR);
            // --- Staff Pool (Mentors & Judges) ---
            User mentor1 = createUser(userRepo, passwordEncoder, "Mentore", "Alpha", "mentor1@hackhub.com", "password", PlatformRole.USER);
            User mentor2 = createUser(userRepo, passwordEncoder, "Mentore", "Beta", "mentor2@hackhub.com", "password", PlatformRole.USER);
            User judge1 = createUser(userRepo, passwordEncoder, "Giudice", "Gamma", "judge1@hackhub.com", "password", PlatformRole.USER);
            User judge2 = createUser(userRepo, passwordEncoder, "Giudice", "Delta", "judge2@hackhub.com", "password", PlatformRole.USER);
            // --- Standard Users (Team Leaders & Members) ---
            User user1 = createUser(userRepo, passwordEncoder, "Mario", "Rossi", "user1@hackhub.com", "password", PlatformRole.USER);
            User user2 = createUser(userRepo, passwordEncoder, "Luigi", "Verdi", "user2@hackhub.com", "password", PlatformRole.USER);
            User user3 = createUser(userRepo, passwordEncoder, "Anna", "Bianchi", "user3@hackhub.com", "password", PlatformRole.USER);
            User user4 = createUser(userRepo, passwordEncoder, "Sofia", "Neri", "user4@hackhub.com", "password", PlatformRole.USER);
            User user5 = createUser(userRepo, passwordEncoder, "Luca", "Gialli", "user5@hackhub.com", "password", PlatformRole.USER);
            User user6 = createUser(userRepo, passwordEncoder, "Giulia", "Viola", "user6@hackhub.com", "password", PlatformRole.USER);
            User user7 = createUser(userRepo, passwordEncoder, "Marco", "Blu", "user7@hackhub.com", "password", PlatformRole.USER);
            User user8 = createUser(userRepo, passwordEncoder, "Elena", "Rosa", "user8@hackhub.com", "password", PlatformRole.USER); // For Invitations

            // =================================================================================
            // 2. HACKATHONS (Different States)
            // =================================================================================
            // A. REGISTRATION OPEN (Managed by Creator 1)
            Hackathon hOpen = createHackathon(hackathonRepo,
                    "Spring Hack 2026",
                    "Hackathon sulla programmazione Java e Spring Boot.",
                    LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(12), LocalDateTime.now().plusDays(5),
                    HackathonStatus.REGISTRATION_OPEN);
            assignStaff(staffRepo, creator1, hOpen, StaffRole.ORGANIZER);
            // B. ONGOING (Managed by Creator 2)
            Hackathon hOngoing = createHackathon(hackathonRepo,
                    "AI Revolution",
                    "Crea la prossima, grande intelligenza artificiale.",
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(2),
                    HackathonStatus.ONGOING);
            assignStaff(staffRepo, creator2, hOngoing, StaffRole.ORGANIZER);
            assignStaff(staffRepo, mentor1, hOngoing, StaffRole.MENTOR);
            assignStaff(staffRepo, mentor2, hOngoing, StaffRole.MENTOR);
            // C. EVALUATION (Managed by Creator 1)
            Hackathon hEval = createHackathon(hackathonRepo,
                    "Green Earth",
                    "Soluzioni sostenibili per il pianeta.",
                    LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(5),
                    HackathonStatus.EVALUATION);
            assignStaff(staffRepo, creator1, hEval, StaffRole.ORGANIZER);
            assignStaff(staffRepo, judge1, hEval, StaffRole.JUDGE);
            assignStaff(staffRepo, judge2, hEval, StaffRole.JUDGE);
            // D. CLOSED (Managed by Creator 2) - Past Event
            Hackathon hClosed = createHackathon(hackathonRepo,
                    "Retro Gaming Jam",
                    "Creazione di giochi stile anni 80.",
                    LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusMonths(1).plusDays(2), LocalDateTime.now().minusMonths(1).minusDays(5),
                    HackathonStatus.FINISHED);
            assignStaff(staffRepo, creator2, hClosed, StaffRole.ORGANIZER);
            // =================================================================================
            // 3. TEAMS & PARTICIPATION
            // =================================================================================
            // --- Team 1: "Spring Masters" (In Open Hackathon) ---
            Team tSpring = createTeam(teamRepo, userRepo, "Spring Masters", user1); // Leader: user1
            addMember(userRepo, user2, tSpring);
            registerTeam(teamRepo, tSpring, hOpen);
            // --- Team 2: "AI Wizards" (In Ongoing Hackathon) ---
            Team tAI = createTeam(teamRepo, userRepo, "AI Wizards", user3); // Leader: user3
            addMember(userRepo, user4, tAI);
            registerTeam(teamRepo, tAI, hOngoing);
            // --- Team 3: "Bot Builders" (In Ongoing Hackathon - Reported) ---
            Team tBot = createTeam(teamRepo, userRepo, "Bot Builders", user5);
            registerTeam(teamRepo, tBot, hOngoing);
            // --- Team 4: "Green Team" (In Evaluation Hackathon - Submitted) ---
            Team tGreen = createTeam(teamRepo, userRepo, "Green Team", user6);
            addMember(userRepo, user7, tGreen);
            registerTeam(teamRepo, tGreen, hEval);
            // =================================================================================
            // 4. INVITATIONS
            // =================================================================================

            // Team "Spring Masters" invites User8 (Pending)
            createInvitation(invitationRepo, tSpring, user8, InvitationStatus.PENDING);

            // Team "AI Wizards" invited user6 (Rejected)
            createInvitation(invitationRepo, tAI, user6, InvitationStatus.REJECTED);
            // =================================================================================
            // 5. OPERATIONAL DATA (Requests, Reports, Submissions)
            // =================================================================================
            // --- Support Requests ---
            // Pending request in Ongoing Hackathon
            createSupportRequest(supportRepo, tAI, hOngoing, "Abbiamo problemi col deploy del modello Python.");

            // Resolved/Scheduled request
            SupportRequest scheduledReq = createSupportRequest(supportRepo, tBot, hOngoing, "API key non funziona.");
            scheduledReq.setResolved(true); // Or use scheduling logic if entity has fields
            supportRepo.save(scheduledReq);
            // --- Violation Reports ---
            // Mentor1 reports Bot Builders
            createViolationReport(reportRepo, hOngoing, mentor1, tBot, "Sospetto uso di codice pre-esistente non dichiarato.");
            // --- Submissions ---
            // Green Team submitted correctly
            Submission subGreen = createSubmission(submissionRepo, tGreen, "https://github.com/greenteam/project", "Sistema IoT per irrigazione smart.");
            // --- Evaluations ---
            // Judge1 evaluates Green Team
            createEvaluation(evaluationRepo, subGreen, judge1, 8, "Buon progetto, codice pulito.");
            // Judge2 evaluates Green Team
            createEvaluation(evaluationRepo, subGreen, judge2, 9, "Ottima idea, scalabile.");
            System.out.println("--- DATA SEEDING COMPLETE ---");
        };
    }
    // --- HELPER METHODS (Optimized with Checks) ---
    // 1. User
    private User createUser(UserRepository repo, PasswordEncoder encoder, String name, String surname, String email, String pwd, PlatformRole role) {
        if (repo.existsByEmail(email)) return repo.findByEmail(email).get();
        User u = new User();
        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setPassword(encoder.encode(pwd));
        u.setPlatformRole(role);
        return repo.save(u);
    }
    // 2. Hackathon
    private Hackathon createHackathon(HackathonRepository repo, String name, String desc, LocalDateTime start, LocalDateTime end, LocalDateTime deadline, HackathonStatus status) {
        if (repo.findByName(name).isPresent()) return repo.findByName(name).get();
        Hackathon h = new Hackathon();
        h.setName(name);
        h.setDescription(desc);
        h.setStartDate(start);
        h.setEndDate(end);
        h.setRegistrationDeadline(deadline);
        h.setStatus(status);
        h.setLocation("Online");
        h.setMaxTeamSize(4);
        h.setPrizeAmount(5000.0);
        return repo.save(h);
    }
    // 3. Staff
    private void assignStaff(StaffAssignmentRepository repo, User user, Hackathon h, StaffRole role) {
        if (repo.existsByUserIdAndHackathonIdAndRole(user.getId(), h.getId(), role)) return;
        repo.save(new StaffAssignment(user, h, role));
    }
    // 4. Team (Note: Handles ManyToOne Leader logic implicitly if model updated)
    private Team createTeam(TeamRepository repo, UserRepository userRepo, String name, User leader) {
        if (repo.findByName(name).isPresent()) return repo.findByName(name).get();
        Team t = new Team(name, leader);
        t = repo.save(t);

        // Update Leader's team reference
        leader.setTeam(t);
        userRepo.save(leader);
        return t;
    }
    private void addMember(UserRepository userRepo, User member, Team team) {
        member.setTeam(team);
        userRepo.save(member);
    }
    private void registerTeam(TeamRepository teamRepo, Team team, Hackathon hackathon) {
        team.setHackathon(hackathon);
        teamRepo.save(team);
    }
    // 5. Invitation
    private void createInvitation(TeamInvitationRepository repo, Team team, User invitee, InvitationStatus status) {
        if (repo.existsByTeamAndInvitee(team, invitee)) return;
        TeamInvitation inv = new TeamInvitation(team, invitee);
        inv.setStatus(status);
        repo.save(inv);
    }
    // 6. Support
    private SupportRequest createSupportRequest(SupportRequestRepository repo, Team team, Hackathon h, String desc) {
        java.util.Optional<SupportRequest> existing = repo.findByTeamAndHackathonAndProblemDescription(team, h, desc);
        if (existing.isPresent()) return existing.get();
        SupportRequest req = new SupportRequest(desc, team, h);
        return repo.save(req);
    }
    // 7. Report
    private void createViolationReport(ViolationReportRepository repo, Hackathon h, User mentor, Team team, String desc) {
        if (repo.existsByReportedTeamAndMentorAndHackathonAndDescription(team, mentor, h, desc)) return;
        ViolationReport rep = new ViolationReport(desc, team, mentor, h);
        repo.save(rep);
    }
    // 8. Submission
    private Submission createSubmission(SubmissionRepository repo, Team team, String url, String desc) {
        if (repo.findByTeamId(team.getId()).isPresent()) return repo.findByTeamId(team.getId()).get();
        Submission sub = new Submission(url, desc, team);
        return repo.save(sub);
    }
    // 9. Evaluation
    private void createEvaluation(EvaluationRepository repo, Submission sub, User judge, int score, String comment) {
        if (repo.existsBySubmissionAndJudge(sub, judge)) return;
        Evaluation eval = new Evaluation();
        eval.setSubmission(sub);
        eval.setJudge(judge);
        eval.setScore(score);
        eval.setComment(comment);
        repo.save(eval);
    }
}