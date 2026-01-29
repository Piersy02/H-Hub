package com.ids.hhub.model;

import com.ids.hhub.model.enums.PlatformRole;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    @JsonIgnore
    private String password; // Hashata
    private String name;
    private String surname;

    @Enumerated(EnumType.STRING)
    private PlatformRole platformRole = PlatformRole.USER;


    // Lista dei ruoli staff (es. Giudice nell'Hackathon A, Mentore nel B)
    @JsonIgnore  //
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StaffAssignment> staffAssignments;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIgnore // se stampo l'utente non voglio ristampare tutto il team
    @ToString.Exclude
    private Team team;
}