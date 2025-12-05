package com.tmc.repository.booking;

import com.tmc.model.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {



    @Query("SELECT COALESCE(SUM(b.allocatedTrucks), 0) FROM Booking b WHERE b.loadId = :loadId AND b.status = com.tmc.model.booking.Status.CONFIRMED")
    Long sumAllocatedTrucksForLoad(UUID loadId);
    Optional<Booking> findByBookingId( UUID bookingId);


}
