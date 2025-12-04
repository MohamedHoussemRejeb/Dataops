package com.pfe.dataops.dataopsapi.catalog.repo;

import com.pfe.dataops.dataopsapi.catalog.entity.OwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<OwnerEntity, Long> {
    Optional<OwnerEntity> findByEmail(String email);

    Optional<OwnerEntity> findByEmailIgnoreCase(String email);
}
