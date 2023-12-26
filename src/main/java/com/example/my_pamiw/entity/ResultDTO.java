package com.example.my_pamiw.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class ResultDTO {

    private Long resultId;

    private String teamHomeName;

    private String teamGuestName;

    private Integer teamHomeGoals;

    private Integer teamGuestGoals;


    public static ResultDTO fromResult(Result result){
        return ResultDTO.builder().resultId(result.getResultId())
                .teamHomeName(result.getTeamHomeName())
                .teamGuestName(result.getTeamGuestName())
                .teamHomeGoals(result.getTeamHomeGoals())
                .teamGuestGoals(result.getTeamGuestGoals())
                .build();
    }
}
