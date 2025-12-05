package com.tmc.repository.bid;

import com.tmc.model.bid.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> , JpaSpecificationExecutor<Bid> {
    
    Optional<Bid> findByBidId(UUID bidId);
    List<Bid> findByLoadIdAndStatus(UUID loadId, com.tmc.model.bid.Status status);  // For uniqueness check
    Page<Bid> findByLoadIdAndTransporterIdAndStatus(UUID loadId, UUID transporterId, String status, Pageable pageable);

    Page<Bid> findByLoadIdAndTransporterId(UUID loadId, UUID transporterId, Pageable pageable);

    Page<Bid> findByLoadIdAndStatus(UUID loadId, String status, Pageable pageable);

    Page<Bid> findByTransporterIdAndStatus(UUID transporterId, String status, Pageable pageable);

    Page<Bid> findByStatus(String status, Pageable pageable);

    Page<Bid> findByTransporterId(UUID transporterId, Pageable pageable);

    Page<Bid> findByLoadId(UUID loadId, Pageable pageable);

    @Query("SELECT b, t FROM Bid b JOIN Transporter t ON b.transporterId = t.transporterId WHERE b.loadId = :loadId AND b.status = com.tmc.model.bid.Status.PENDING")
    List<Object[]> findActiveBidsWithTransporterByLoadId(@Param("loadId") UUID loadId);


}
