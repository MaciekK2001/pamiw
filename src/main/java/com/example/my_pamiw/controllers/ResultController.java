package com.example.my_pamiw.controllers;

import com.example.my_pamiw.entity.ResultDTO;
import com.example.my_pamiw.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin("*")
public class ResultController {

    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping
    public ResponseEntity<List<ResultDTO>> getAllResults() {
        List<ResultDTO> results = resultService.getAllResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<ResultDTO> getResultById(@PathVariable Long resultId) {
        return resultService.getResultById(resultId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createResult(@RequestBody ResultDTO resultDTO) {
        ResultDTO savedResult = resultService.createResult(resultDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResult);
    }

    @DeleteMapping("/{resultId}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long resultId) {
        resultService.deleteResult(resultId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{resultId}")
    public ResponseEntity<String> updateResult(@PathVariable Long resultId, @RequestBody ResultDTO updatedResult) {
        try {
            resultService.updateResult(resultId, updatedResult);
            return ResponseEntity.ok("Result updated successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}