package com.tmc.repository.transporter;

import com.tmc.model.transporter.AvailableTrucks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailableTrucksRepository extends JpaRepository<AvailableTrucks,Long> {
    AvailableTrucks findByTransporter_TransporterIdAndTruckType(UUID transporterId, String truckType);


    List<AvailableTrucks> findByTransporter_TransporterId(UUID transporterId);
}
