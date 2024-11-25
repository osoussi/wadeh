package com.exemple.backend_spring.service;

import com.exemple.backend_spring.Security.Repository.UserRepository;
import com.exemple.backend_spring.dto.PropositionDTO;
import com.exemple.backend_spring.model.Proposition;
import com.exemple.backend_spring.repository.PropositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropositionService {


    private final PropositionRepository propositionRepository;
    private final UserRepository userRepository;

    @Value("${upload.dir}") // Chemin du répertoire pour stocker les fichiers téléchargés
    private String uploadDir;

    public PropositionDTO createProposition(PropositionDTO propositionDTO, MultipartFile file) throws IOException {
        String filePath = storeFile(file);
        Proposition proposition = new Proposition();
        proposition.setTitle(propositionDTO.getTitle());
        proposition.setDocument(filePath);
        proposition.setUserId(propositionDTO.getUserId());
        proposition.setPublicationDate(LocalDateTime.now());
        proposition.setStatus(false);
        Proposition savedProposition = propositionRepository.save(proposition);
        return mapToDTO(savedProposition);
    }

    public List<PropositionDTO> getAllPropositions() {
        return propositionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PropositionDTO getPropositionById(Long id) {
        Proposition proposition = propositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposition not found"));
        return mapToDTO(proposition);
    }
    public void validateOwnership(Long propositionId, Long userId) {
        Proposition proposition = propositionRepository.findById(propositionId)
                .orElseThrow(() -> new RuntimeException("Proposition not found"));
        if (!proposition.getUserId().equals(userId)) {
            throw new RuntimeException("You do not have permission to modify or delete this proposition.");
        }
    }

    public void deleteProposition(Long id, Long userId) {
        validateOwnership(id, userId); // Ensure the user owns the proposition
        propositionRepository.deleteById(id);
    }

    /*public PropositionDTO updateProposition(Long id, PropositionDTO propositionDTO, Long userId) {
        validateOwnership(id, userId); // Ensure the user owns the proposition
        Proposition proposition = propositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposition not found"));
        proposition.setTitle(propositionDTO.getTitle());
        proposition.setDocument(propositionDTO.getDocument());
        Proposition updatedProposition = propositionRepository.save(proposition);
        return mapToDTO(updatedProposition);
    }*/

    public PropositionDTO updateProposition(Long id, Long userId, String title, MultipartFile file) throws IOException {
        validateOwnership(id, userId);

        Proposition proposition = propositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposition not found"));

        if (title != null) {
            proposition.setTitle(title);
        }

        if (file != null && !file.isEmpty()) {
            String filePath = storeFile(file);
            proposition.setDocument(filePath);
        }

        Proposition updatedProposition = propositionRepository.save(proposition);
        return mapToDTO(updatedProposition);
    }



    public List<PropositionDTO> getPropositionsByUser(Long userId) {
        return propositionRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PropositionDTO validateProposition(Long id) {
        Proposition proposition = propositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposition not found"));

        // Set status to true
        proposition.setStatus(true);

        Proposition updatedProposition = propositionRepository.save(proposition);
        return mapToDTO(updatedProposition);
    }

    public List<PropositionDTO> getValidatedPropositions() {
        return propositionRepository.findByStatus(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    /*public PropositionDTO updateProposition(Long id, PropositionDTO propositionDTO) {
        Proposition proposition = propositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposition not found"));
        proposition.setTitle(propositionDTO.getTitle());
        proposition.setDocument(propositionDTO.getDocument()); // Vous pouvez gérer le fichier ici si nécessaire
        Proposition updatedProposition = propositionRepository.save(proposition);
        return mapToDTO(updatedProposition);
    }*/

/*
    public void deleteProposition(Long id) {
        propositionRepository.deleteById(id);
    }
*/

    private String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Ensure the directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // Create directory if it doesn't exist
        }

        // Generate a unique file name
        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationPath = uploadPath.resolve(uniqueFileName);

        // Copy the file
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString(); // Return the stored file's path
    }


    private PropositionDTO mapToDTO(Proposition proposition) {
        PropositionDTO dto = new PropositionDTO();
        dto.setId(proposition.getId());
        dto.setTitle(proposition.getTitle());
        dto.setDocument(proposition.getDocument());
        dto.setStatus(proposition.isStatus());
        dto.setUserId(proposition.getUserId());

        // Fetch user profile and set it in the DTO
        userRepository.findById(proposition.getUserId()).ifPresent(user -> dto.setUserProfile(user.getUserprofile()));

        return dto;
    }

}