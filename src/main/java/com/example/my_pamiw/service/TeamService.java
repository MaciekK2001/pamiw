package com.example.my_pamiw.service;

import com.example.my_pamiw.entity.Result;
import com.example.my_pamiw.entity.Team;
import com.example.my_pamiw.entity.TeamDTO;
import com.example.my_pamiw.repository.ResultRepository;
import com.example.my_pamiw.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private ResultService resultService;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private TeamRepository teamRepository;


    public List<TeamDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(TeamDTO::fromTeam)
                .collect(Collectors.toList());
    }

    public Optional<TeamDTO> getTeamById(Long teamId) {
        if(teamRepository.findById(teamId).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The team of given id doesn't exist in database");

        return teamRepository.findById(teamId).map(TeamDTO::fromTeam);
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {

        if(teamRepository.findByTeamName(teamDTO.getTeamName()) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The team of given name already exists in database");
        }

        if (teamDTO.getWins() == null) {
            teamDTO.setWins(0);
        }

        if (teamDTO.getDraws() == null) {
            teamDTO.setDraws(0);
        }

        if (teamDTO.getLoses() == null) {
            teamDTO.setLoses(0);
        }

        if (teamDTO.getGoalsScored() == null) {
            teamDTO.setGoalsScored(0);
        }

        if (teamDTO.getGoalsLost() == null) {
            teamDTO.setGoalsLost(0);
        }

        Team team = Team.builder()
                .teamName(teamDTO.getTeamName())
                .leagueName(teamDTO.getLeagueName())
                .wins(teamDTO.getWins())
                .draws(teamDTO.getDraws())
                .loses(teamDTO.getLoses())
                .goalsScored(teamDTO.getGoalsScored())
                .goalsLost(teamDTO.getGoalsLost())
                .build();

        Team savedTeam = teamRepository.save(team);
        return TeamDTO.fromTeam(savedTeam);
    }

    public void deleteTeam(Long teamId) {

        if(!teamRepository.findById(teamId).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The team of given id doesn't exist in database");

        for (Result result : resultRepository.findAll()) {

            if(result.getTeamHome().getTeamId().equals(teamId) || result.getTeamGuest().getTeamId().equals(teamId))
                resultService.deleteResult(result.getResultId());
        }

        teamRepository.deleteById(teamId);
    }

    public void updateTeam(TeamDTO updatedTeam, Long teamId) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if(teamRepository.findById(teamId).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The team of given id doesn't exist in database");

        if (optionalTeam.isPresent()) {
            Team existingTeam = optionalTeam.get();

            if(updatedTeam.getTeamName() != null) {
                existingTeam.setTeamName(updatedTeam.getTeamName());
                for (Result result : resultRepository.findAll()) {

                    if(result.getTeamHome().getTeamId().equals(teamId))
                        result.setTeamHomeName(updatedTeam.getTeamName());
                    if(result.getTeamGuest().getTeamId().equals(teamId))
                        result.setTeamGuestName(updatedTeam.getTeamName());
                }
            }

            if(updatedTeam.getLeagueName()!= null)
                existingTeam.setLeagueName(updatedTeam.getLeagueName());

            teamRepository.save(existingTeam);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }
    }

}