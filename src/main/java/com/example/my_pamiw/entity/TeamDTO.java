package com.example.my_pamiw.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TeamDTO {
    private Long teamId;

    private String teamName;

    private String leagueName;

    private Integer wins;

    private Integer draws;

    private Integer loses;

    private Integer goalsScored;

    private Integer goalsLost;

    public static TeamDTO fromTeam(Team team) {
        return TeamDTO.builder()
                .teamId(team.getTeamId())
                .teamName(team.getTeamName())
                .leagueName(team.getLeagueName())
                .wins(team.getWins())
                .draws(team.getDraws())
                .loses(team.getLoses())
                .goalsScored(team.getGoalsScored())
                .goalsLost(team.getGoalsLost())
                .build();
    }
}
