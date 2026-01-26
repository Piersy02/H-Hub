package com.ids.hhub.service;


import com.ids.hhub.model.Hackathon;
import com.ids.hhub.model.Team;
import com.ids.hhub.model.TeamInvitation;
import com.ids.hhub.model.User;
import com.ids.hhub.model.enums.InvitationStatus;
import com.ids.hhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    @Autowired private TeamRepository teamRepo;
    @Autowired private HackathonRepository hackathonRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private TeamInvitationRepository invitationRepo;
    @Autowired private SubmissionRepository submissionRepo;

    // STEP 1: CREAZIONE DEL TEAM (Senza Hackathon)
    @Transactional
    public Team createTeam(String teamName, String leaderEmail) {
        User leader = userRepo.findByEmail(leaderEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (leader.getTeam() != null) {
            throw new RuntimeException("Sei già in un team! Esci prima di crearne uno.");
        }

        // Crea il team "libero"
        Team team = new Team(teamName, leader);
        team = teamRepo.save(team);

        // Collega il leader
        leader.setTeam(team);
        team.getMembers().add(leader); // Aggiungi il leader alla lista membri
        userRepo.save(leader);

        return team;
    }

    // STEP 2: ISCRIZIONE ALL'HACKATHON
    @Transactional
    public void registerTeamByName(String teamName, String hackathonName, String requesterEmail) {
        // Cerca il Team per nome
        Team team = teamRepo.findByName(teamName)
                .orElseThrow(() -> new RuntimeException("Team non trovato con nome: " + teamName));

        // Cerca l'Hackathon per nome
        Hackathon hackathon = hackathonRepo.findByName(hackathonName)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato con nome: " + hackathonName));

        // Recupera chi fa la richiesta
        User requester = userRepo.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Controllo: Solo il Leader può iscrivere il team
        if (!team.getLeader().equals(requester)) {
            throw new SecurityException("Solo il Leader può iscrivere il team a un evento!");
        }

        // Controllo: Il team è già iscritto a qualcosa?
        if (team.getHackathon() != null) {
            throw new RuntimeException("Il team è già iscritto a un altro Hackathon!");
        }

        // STATE PATTERN CHECK
        // Questo metodo lancerà eccezione se lo stato non è REGISTRATION_OPEN
        hackathon.registerTeam(team);

        // Se tutto ok, salva l'associazione
        team.setHackathon(hackathon);
        teamRepo.save(team);
    }

    //  IL LEADER INVIA L'INVITO
    @Transactional
    public void sendInvitation(Long teamId, String inviteeEmail, String leaderEmail) {
        // Controlli sul Team e Leader
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        User leader = userRepo.findByEmail(leaderEmail).orElseThrow();

        if (!team.getLeader().equals(leader)) {
            throw new SecurityException("Solo il Leader può inviare inviti!");
        }

        // Controlli sull'Invitato
        User invitee = userRepo.findByEmail(inviteeEmail)
                .orElseThrow(() -> new RuntimeException("Utente da invitare non trovato"));

        if (invitee.getTeam() != null) {
            throw new RuntimeException("L'utente fa già parte di un altro team!");
        }

        // Crea l'invito (PENDING)
        TeamInvitation invitation = new TeamInvitation(team, invitee);
        invitationRepo.save(invitation);
    }

    //  L'UTENTE ACCETTA L'INVITO
    @Transactional
    public void acceptInvitation(Long invitationId, String userEmail) {
        // Recupera l'invito
        TeamInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invito non trovato"));

        // Verifica che chi accetta sia davvero il destinatario
        if (!invitation.getInvitee().getEmail().equals(userEmail)) {
            throw new SecurityException("Non puoi accettare un invito non tuo!");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invito non più valido.");
        }

        Team team = invitation.getTeam();
        User user = invitation.getInvitee();

        // STATE PATTERN CHECK
        // Se nel frattempo l'hackathon è iniziato, l'accettazione deve fallire.
        if (team.getHackathon() != null) {
            team.getHackathon().getCurrentStateObject().registerTeam(team.getHackathon(), team);
        }

        // Esegui l'unione
        team.getMembers().add(user);
        user.setTeam(team);

        // Chiudi l'invito
        invitation.setStatus(InvitationStatus.ACCEPTED);

        userRepo.save(user);
        teamRepo.save(team);
        invitationRepo.save(invitation);
    }

    // VEDERE I PROPRI INVITI
    public List<TeamInvitation> getMyPendingInvitations(String userEmail) {
        return invitationRepo.findByInviteeEmailAndStatus(userEmail, InvitationStatus.PENDING);
    }

    @Transactional
    public void rejectInvitation(Long invitationId, String userEmail) {
        // Recupera l'invito
        TeamInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invito non trovato"));

        // Verifica che chi rifiuta sia davvero il destinatario
        if (!invitation.getInvitee().getEmail().equals(userEmail)) {
            throw new SecurityException("Non puoi rifiutare un invito non tuo!");
        }

        // Verifica che l'invito sia ancora in attesa
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("L'invito non è più valido (già accettato o rifiutato).");
        }

        // Cambia lo stato in REJECTED
        invitation.setStatus(InvitationStatus.REJECTED);

        // Salva
        invitationRepo.save(invitation);
    }
    // --- 4. VISUALIZZA IL MIO TEAM ---
    public Team getMyTeam(String userEmail) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        if (user.getTeam() == null) {
            throw new RuntimeException("Non fai parte di nessun team al momento.");
        }
        return user.getTeam();
    }

    // --- 5. ABBANDONA TEAM (Per i membri) ---
    @Transactional
    public void leaveTeam(String userEmail) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Team team = user.getTeam();

        if (team == null) {
            throw new RuntimeException("Non sei in nessun team.");
        }

        // Se sei il Leader, non puoi abbandonare (devi prima cancellare il team o passare la leadership)
        // Per semplicità Il leader scioglie il team se esce.
        if (team.getLeader().equals(user)) {
            deleteTeam(team.getId(), userEmail); // Richiama il metodo di cancellazione
            return;
        }

        // Controllo Stato Hackathon (Se iscritto)
        if (team.getHackathon() != null) {
            // Se l'hackathon è iniziato, non si può abbandonare (o forse sì? Dipende dalle regole.
            // Di solito si blocca per evitare cambi squadra in corsa).
            // team.getHackathon().getCurrentStateObject().registerTeam(...) // Check opzionale
        }

        // Rimuovi
        team.getMembers().remove(user);
        user.setTeam(null);

        userRepo.save(user);
        teamRepo.save(team);
    }

    // --- 6. RIMUOVI MEMBRO (Solo Leader) ---
    @Transactional
    public void kickMember(Long teamId, Long memberId, String leaderEmail) {
        Team team = teamRepo.findById(teamId).orElseThrow();
        User leader = userRepo.findByEmail(leaderEmail).orElseThrow();

        // Controllo Leader
        if (!team.getLeader().equals(leader)) {
            throw new SecurityException("Solo il Leader può espellere i membri.");
        }

        User memberToRemove = userRepo.findById(memberId).orElseThrow();

        if (!team.getMembers().contains(memberToRemove)) {
            throw new RuntimeException("L'utente non fa parte di questo team.");
        }

        if (memberToRemove.equals(leader)) {
            throw new RuntimeException("Non puoi espellere te stesso (usa 'Abbandona' o 'Elimina Team').");
        }

        // Rimuovi
        team.getMembers().remove(memberToRemove);
        memberToRemove.setTeam(null);

        userRepo.save(memberToRemove);
        teamRepo.save(team);
    }

    // --- 7. ELIMINA TEAM (Solo Leader) ---
    @Transactional
    public void deleteTeam(Long teamId, String requesterEmail) {
        Team team = teamRepo.findById(teamId).orElseThrow();
        User requester = userRepo.findByEmail(requesterEmail).orElseThrow();

        if (!team.getLeader().equals(requester)) {
            throw new SecurityException("Solo il Leader può sciogliere il team.");
        }

        // Libera tutti i membri
        for (User member : team.getMembers()) {
            member.setTeam(null);
            userRepo.save(member);
        }

        // Pulisci la lista per evitare errori JPA
        team.getMembers().clear();

        // Se c'è una sottomissione, va cancellata
        if (team.getSubmission() != null) {
            submissionRepo.delete(team.getSubmission());
        }

        teamRepo.delete(team);
    }


}
