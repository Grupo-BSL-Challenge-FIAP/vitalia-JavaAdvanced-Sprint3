package br.com.clyvo.vitalia.repository;

import br.com.clyvo.vitalia.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository
        extends JpaRepository<Breed, Long> {

    List<Breed> findBySpeciesIdOrderByNameAsc(
            Long speciesId
    );
}