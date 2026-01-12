package com.ids.hhub.service;


import com.ids.hhub.model.Hackathon;
import com.ids.hhub.model.Team;
import com.ids.hhub.model.TeamInvitation;
import com.ids.hhub.model.User;
import com.ids.hhub.model.enums.InvitationStatus;
import com.ids.hhub.repository.HackathonRepository;
import com.ids.hhub.repository.TeamInvitationRepository;
import com.ids.hhub.repository.TeamRepository;
import com.ids.hhub.repository.UserRepository;
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

}
