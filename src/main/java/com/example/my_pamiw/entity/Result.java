package com.example.my_pamiw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @ManyToOne
    private Team teamHome;

    @ManyToOne
    private Team teamGuest;

    private String teamHomeName;

    private String teamGuestName;

    private Integer teamHomeGoals;

    private Integer teamGuestGoals;

}
