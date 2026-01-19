package com.ids.hhub.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateHackathonDto {

    private String name;
    private String description;

    // Campi aggiuntivi richiesti dal progetto
    private String rules;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")// Regolamento
    private LocalDateTime registrationDeadline; // Scadenza iscrizioni
    private int maxTeamSize;                // Dimensione massima team

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private double prizeAmount;
}
