package com.tmc.repository.load;

import com.tmc.dto.load.LoadResponse;
import com.tmc.model.load.Load;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {
    Optional<Load> findByLoadId(UUID loadId);

    Page<Load> findByShipperIdAndStatus(UUID shipperId, String status, Pageable pageable);

    Page<Load> findByShipperId(UUID shipperId, Pageable pageable);

    Page<Load> findByStatus(String status, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Load l where l.loadId = :loadId")
    Load lockLoad(UUID loadId);
}
