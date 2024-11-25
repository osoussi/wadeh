package com.exemple.backend_spring.controller;

import com.exemple.backend_spring.dto.PropositionDTO;
import com.exemple.backend_spring.service.PropositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/propositions")
public class PropositionController {


    private final PropositionService propositionService;

    @PostMapping
    public ResponseEntity<PropositionDTO> createProposition(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("title") String title,
                                                            @RequestParam("userId") Long userId) throws IOException {
        PropositionDTO propositionDTO = new PropositionDTO();
        propositionDTO.setTitle(title);
        propositionDTO.setUserId(userId);
        PropositionDTO createdProposition = propositionService.createProposition(propositionDTO, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProposition);
    }

    @GetMapping
    public ResponseEntity<List<PropositionDTO>> getAllPropositions() {
        List<PropositionDTO> propositions = propositionService.getAllPropositions();
        return ResponseEntity.ok(propositions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropositionDTO> getPropositionById(@PathVariable Long id) {
        PropositionDTO proposition = propositionService.getPropositionById(id);
        return ResponseEntity.ok(proposition);
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<PropositionDTO> updateProposition(@PathVariable Long id, @RequestBody PropositionDTO propositionDTO) {
        PropositionDTO updatedProposition = propositionService.updateProposition(id, propositionDTO);
        return ResponseEntity.ok(updatedProposition);
    }*/

    /*@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposition(@PathVariable Long id) {
        propositionService.deleteProposition(id);
        return ResponseEntity.noContent().build();
    }*/

    /*@PutMapping("/{id}")
    public ResponseEntity<PropositionDTO> updateProposition(
            @PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestBody PropositionDTO propositionDTO) {
        PropositionDTO updatedProposition = propositionService.updateProposition(id, propositionDTO, userId);
        return ResponseEntity.ok(updatedProposition);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<PropositionDTO> updateProposition(
            @PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "title", required = false) String title,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        PropositionDTO updatedProposition = propositionService.updateProposition(id, userId, title, file);
        return ResponseEntity.ok(updatedProposition);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposition(@PathVariable Long id, @RequestParam("userId") Long userId) {
        propositionService.deleteProposition(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PropositionDTO>> getPropositionsByUser(@PathVariable Long userId) {
        List<PropositionDTO> propositions = propositionService.getPropositionsByUser(userId);
        return ResponseEntity.ok(propositions);
    }

    @PatchMapping("/{id}/validate")
    public ResponseEntity<PropositionDTO> validateProposition(@PathVariable Long id) {
        PropositionDTO updatedProposition = propositionService.validateProposition(id);
        return ResponseEntity.ok(updatedProposition);
    }

    @GetMapping("/validated")
    public ResponseEntity<List<PropositionDTO>> getValidatedPropositions() {
        List<PropositionDTO> validatedPropositions = propositionService.getValidatedPropositions();
        return ResponseEntity.ok(validatedPropositions);
    }




}
