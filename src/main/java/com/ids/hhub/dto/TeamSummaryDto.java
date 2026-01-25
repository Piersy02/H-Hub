package com.ids.hhub.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class TeamSummaryDto {
    private Long teamId;
    private String teamName;
    private String leaderEmail;
    private String leaderName;
    private List<TeamMemberDto> members;
}