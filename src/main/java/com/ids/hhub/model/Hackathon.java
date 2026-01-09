package com.ids.hhub.model;

import com.ids.hhub.model.enums.HackathonStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String rules;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime registrationDeadline;

    private int maxTeamSize;
    private double prizeAmount;

    @Enumerated(EnumType.STRING)
    private HackathonStatus state = HackathonStatus.REGISTRATION_OPEN;

    // Team iscritti a questo hackathon
    @OneToMany(mappedBy = "hackathon")
    private List<Team> teams;

    // Staff assegnato (Organizzatore, Giudici, Mentori)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<StaffAssignment> staff;

    // Vincitore (solo quando concluso)
    @OneToOne
    private Team winner;
}