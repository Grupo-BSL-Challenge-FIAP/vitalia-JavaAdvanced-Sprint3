package br.com.clyvo.vitalia.repository;

import br.com.clyvo.vitalia.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p WHERE p.owner.id = :ownerId")
    Page<Pet> findByOwnerUserId(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );

    Page<Pet> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
}