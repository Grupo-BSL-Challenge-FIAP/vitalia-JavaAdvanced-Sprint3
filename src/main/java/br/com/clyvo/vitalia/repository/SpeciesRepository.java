package br.com.clyvo.vitalia.repository;

import br.com.clyvo.vitalia.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository
        extends JpaRepository<Species, Long> {
}