package com.example.my_pamiw.service;

import com.example.my_pamiw.entity.Result;
import com.example.my_pamiw.entity.ResultDTO;
import com.example.my_pamiw.entity.Team;
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
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<ResultDTO> getAllResults() {
        List<Result> results = resultRepository.findAll();
        return results.stream()
                .map(ResultDTO::fromResult)
                .collect(Collectors.toList());
    }

    public Optional<ResultDTO> getResultById(Long resultId) {
        if(teamRepository.findById(resultId).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The result of given id doesn't exist in database");
        return resultRepository.findById(resultId).map(ResultDTO::fromResult);
    }

    public ResultDTO createResult(ResultDTO resultDTO) {

        if(teamRepository.findByTeamName(resultDTO.getTeamGuestName()) == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The result of given id doesn't exist in database");
        }

        if(teamRepository.findByTeamName(resultDTO.getTeamHomeName()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The result of given id doesn't exist in database");
        }
        
        if (resultDTO.getTeamGuestGoals() == null) {
            resultDTO.setTeamGuestGoals(0);
        }

        if (resultDTO.getTeamHomeGoals() == null) {
            resultDTO.setTeamHomeGoals(0);
        }

        Result result = Result.builder()
                .teamHomeName(resultDTO.getTeamHomeName())
                .teamGuestName(resultDTO.getTeamGuestName())
                .teamHomeGoals(resultDTO.getTeamHomeGoals())
                .teamGuestGoals(resultDTO.getTeamGuestGoals())
                .build();


        result.setTeamHome(teamRepository.findByTeamName(resultDTO.getTeamHomeName()));
        result.setTeamGuest(teamRepository.findByTeamName(resultDTO.getTeamGuestName()));

        Team teamHome = teamRepository.findByTeamName(result.getTeamHomeName());
        Team teamGuest = teamRepository.findByTeamName(result.getTeamGuestName());

        updatingTeamStats(teamHome, teamGuest, result, "A");

        Result savedResult = resultRepository.save(result);
        return ResultDTO.fromResult(savedResult);
    }
    public void updateResult(Long resultId, ResultDTO updatedResult){
        if(teamRepository.findById(resultId).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The result of given id doesn't exist in database");

        Result result = resultRepository.findById(resultId).get();

        if(teamRepository.findByTeamName(updatedResult.getTeamHomeName()) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The teamHomeName in updatedResult doesn't exist in database");
        if(teamRepository.findByTeamName(updatedResult.getTeamGuestName()) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The teamGuestName in updatedResult doesn't exist in database");


        updatingResult(result, updatedResult);
    }

    public void deleteResult(Long resultId) {
        Optional<Result> result = resultRepository.findById(resultId);

        if(!result.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The result of given id doesn't exist in database");

        Team teamHome = teamRepository.findByTeamName(result.get().getTeamHomeName());
        Team teamGuest = teamRepository.findByTeamName(result.get().getTeamGuestName());


        updatingTeamStats(teamHome, teamGuest, result.get(), "D");

        resultRepository.deleteById(resultId);


    }

    private void updatingTeamStats(Team teamHome, Team teamGuest, Result result, String option){

        if(option == "A"){
            if(result.getTeamHomeGoals() > result.getTeamGuestGoals()){
                teamHome.setWins(teamHome.getWins() + 1);
                teamGuest.setLoses(teamGuest.getLoses() + 1);
            } else if (result.getTeamHomeGoals() < result.getTeamGuestGoals()){
                teamHome.setLoses(teamHome.getLoses() + 1);
                teamGuest.setWins(teamGuest.getWins() + 1);
            } else {
                teamHome.setDraws(teamHome.getDraws() + 1);
                teamGuest.setDraws(teamGuest.getDraws() + 1);
            }

            teamHome.setGoalsScored(teamHome.getGoalsScored() + result.getTeamHomeGoals());
            teamHome.setGoalsLost(teamHome.getGoalsLost() + result.getTeamGuestGoals());

            teamGuest.setGoalsScored(teamGuest.getGoalsScored() + result.getTeamGuestGoals());
            teamGuest.setGoalsLost(teamGuest.getGoalsLost() + result.getTeamHomeGoals());
        }

        if(option == "D"){
            //updating teams stats
            if(result.getTeamHomeGoals() > result.getTeamGuestGoals()){
                teamHome.setWins(teamHome.getWins() - 1);
                teamGuest.setLoses(teamGuest.getLoses() - 1);
            } else if (result.getTeamHomeGoals() < result.getTeamGuestGoals()){
                teamHome.setLoses(teamHome.getLoses() - 1);
                teamGuest.setWins(teamGuest.getWins() - 1);
            } else {
                teamHome.setDraws(teamHome.getDraws() - 1);
                teamGuest.setDraws(teamGuest.getDraws() - 1);
            }

            teamHome.setGoalsScored(teamHome.getGoalsScored() - result.getTeamHomeGoals());
            teamHome.setGoalsLost(teamHome.getGoalsLost() - result.getTeamGuestGoals());

            teamGuest.setGoalsScored(teamGuest.getGoalsScored() - result.getTeamGuestGoals());
            teamGuest.setGoalsLost(teamGuest.getGoalsLost() - result.getTeamHomeGoals());
        }

    }

    private void updatingResult(Result result, ResultDTO updatedResult){
        //removing current result stats from teams
        Team teamHome = teamRepository.findByTeamName(result.getTeamHomeName());
        Team teamGuest = teamRepository.findByTeamName(result.getTeamGuestName());

        updatingTeamStats(teamHome, teamGuest, result, "D");

        //updating result
        result.setTeamHome(teamRepository.findByTeamName(updatedResult.getTeamHomeName()));
        result.setTeamGuest(teamRepository.findByTeamName(updatedResult.getTeamGuestName()));
        result.setTeamHomeGoals(updatedResult.getTeamHomeGoals());
        result.setTeamGuestGoals(updatedResult.getTeamGuestGoals());

        Team newTeamHome = teamRepository.findByTeamName(updatedResult.getTeamHomeName());
        Team newTeamGuest = teamRepository.findByTeamName(updatedResult.getTeamGuestName());

        updatingTeamStats(newTeamHome, newTeamGuest, result, "A");

        teamRepository.save(teamHome);
        teamRepository.save(teamGuest);
        teamRepository.save(newTeamHome);
        teamRepository.save(newTeamGuest);
        resultRepository.save(result);
    }
}
