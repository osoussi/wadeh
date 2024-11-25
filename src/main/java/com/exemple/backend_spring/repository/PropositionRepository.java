package com.exemple.backend_spring.repository;

import com.exemple.backend_spring.model.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface PropositionRepository extends JpaRepository<Proposition, Long> {
    List<Proposition> findByUserId(Long userId);
    List<Proposition> findByStatus(boolean status);
}