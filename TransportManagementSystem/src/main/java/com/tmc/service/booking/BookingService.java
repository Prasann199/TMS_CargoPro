package com.tmc.service.booking;

import com.tmc.dto.booking.ApiResponse;
import com.tmc.dto.booking.BookingRequest;
import com.tmc.dto.booking.BookingResponse;
import com.tmc.exception.InsufficientCapacityException;
import com.tmc.exception.InvalidStatusTransitionException;
import com.tmc.exception.LoadAlreadyBookedException;
import com.tmc.exception.ResourceNotFoundException;
import com.tmc.model.bid.Bid;
import com.tmc.model.booking.Booking;
import com.tmc.model.load.Load;
import com.tmc.model.load.Status;
import com.tmc.model.transporter.AvailableTrucks;
import com.tmc.model.transporter.Transporter;
import com.tmc.repository.bid.BidRepository;
import com.tmc.repository.booking.BookingRepository;
import com.tmc.repository.load.LoadRepository;
import com.tmc.repository.transporter.AvailableTrucksRepository;

import com.tmc.repository.transporter.TransporterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class BookingService {

    @Autowired
    public BookingRepository bookingRepository;

    @Autowired
    public LoadRepository loadRepository;

    @Autowired
    public BidRepository bidRepository;

    @Autowired
    public TransporterRepository transporterRepository;

    @Autowired
    public AvailableTrucksRepository availableTrucksRepository;

    @Transactional
    public ApiResponse<BookingResponse> createBooking(BookingRequest request) {
        log.info("Creating booking for bidId: {}", request.getBidId());
            Bid bid=bidRepository.findByBidId(request.getBidId())
                    .orElseThrow(()->  new ResourceNotFoundException("Bid not found for this BidId :"+request.getBidId()));
            if(bid.getStatus()!= com.tmc.model.bid.Status.PENDING){
                throw new InvalidStatusTransitionException("Bid status must be pending for booking!");
            }
            Load load=loadRepository.findByLoadId(bid.getLoadId())
                    .orElseThrow(()->{
                            throw new ResourceNotFoundException("Load not found with this bid!");
                    });

            if(load.getStatus()==Status.BOOKED || load.getStatus()==Status.CANCELLED){
                throw new InvalidStatusTransitionException("Cannot book on CANCELLED or BOOKED load");
            }
            Transporter transporter=transporterRepository.findByTransporterId(bid.getTransporterId())
                    .orElseThrow(()->new ResourceNotFoundException("Transporter not found!"));
            AvailableTrucks availableTrucks = availableTrucksRepository
                    .findByTransporter_TransporterIdAndTruckType(bid.getTransporterId(), load.getTruckType());

            if (availableTrucks == null || availableTrucks.getCount() <= 0) {
                throw new InsufficientCapacityException("Transporter has no available trucks of type " + load.getTruckType());
            }
            int allocate = (request.getAllocatedTrucks() > 0) ? request.getAllocatedTrucks() : bid.getTrucksOffered();
            if(availableTrucks.getCount()<allocate){
                throw new InsufficientCapacityException("Not enough available trucks. Available :"+availableTrucks+",Required :"+allocate);
            }
            // Multi-Truck Allocation: Check remaining trucks
            Long totalAllocated = bookingRepository.sumAllocatedTrucksForLoad(load.getLoadId());
            int remainingTrucks = load.getNoOfTrucks() - totalAllocated.intValue();
            if (allocate > remainingTrucks) {
                throw new InsufficientCapacityException("Not enough trucks remaining for load. Remaining: " + remainingTrucks + ", Requested: " + allocate);
            }
            // Unique Accepted Bid: Ensure no other ACCEPTED bid for this load
            List<Bid> acceptedBids = bidRepository.findByLoadIdAndStatus(load.getLoadId(), com.tmc.model.bid.Status.ACCEPTED);
            if (!acceptedBids.isEmpty()) {
                throw new InvalidStatusTransitionException("Load already has an accepted bid");
            }
            // Deduct trucks
            availableTrucks.setCount(availableTrucks.getCount() - allocate);
            availableTrucksRepository.save(availableTrucks);

            // Create booking
            Booking booking = Booking.builder()
                    .loadId(load.getLoadId())
                    .bidId(bid.getBidId())
                    .transporterId(transporter.getTransporterId())
                    .allocatedTrucks(allocate)
                    .finalRate((request.getFinalRate() > 0) ? request.getFinalRate() : bid.getProposedRate())
                    .status(com.tmc.model.booking.Status.CONFIRMED)
                    .build();
            bookingRepository.save(booking);

            // Update bid status to ACCEPTED
            bid.setStatus(com.tmc.model.bid.Status.ACCEPTED);
            bidRepository.save(bid);

            // Update load status (optimistic locking prevents concurrency)
            if (remainingTrucks - allocate == 0) {
                load.setStatus(Status.BOOKED);
            } else if (load.getStatus() == Status.POSTED) {
                load.setStatus(Status.OPEN_FOR_BIDS);
            }
            loadRepository.save(load);

            log.info("Booking created successfully: {}", booking.getBookingId());
            ApiResponse<BookingResponse> apiResponse= ApiResponse.<BookingResponse>builder()
                    .message("Booking created successfully!")
                    .data(mapToResponse(booking))
                    .status("CREATED")
                    .build();
            return apiResponse;



    }


    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response=BookingResponse.builder()
                .bookedAt(booking.getBookedAt())
                .bookingId(booking.getBookingId())
                .allocatedTrucks(booking.getAllocatedTrucks())
                .bidId(booking.getBidId())
                .finalRate(booking.getFinalRate())
                .loadId(booking.getLoadId())
                .status(booking.getStatus())
                .transporterId(booking.getTransporterId())
                .build();
        return  response;
    }
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID bookingId) {
            Booking booking=bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(()-> new ResourceNotFoundException("Booking not found!"));
            return mapToResponse(booking);

    }
    @Transactional
    public BookingResponse cancelBooking(UUID bookingId) {
        Booking booking=bookingRepository.findByBookingId(bookingId)
                .orElseThrow(()-> new ResourceNotFoundException("Booking not found!"));
        if(booking.getStatus()!= com.tmc.model.booking.Status.CONFIRMED){
            throw  new InvalidStatusTransitionException("Only confirmed booking can be cancelled!");
        }

            AvailableTrucks availableTrucks=availableTrucksRepository
                    .findByTransporter_TransporterIdAndTruckType(booking.getTransporterId(),
                            loadRepository.findById(booking.getLoadId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Load not found: " + booking.getLoadId()))
                                    .getTruckType());
                if(availableTrucks==null){
                    throw  new ResourceNotFoundException("available trucks not found!");
            }
            availableTrucks.setCount(availableTrucks.getCount() + booking.getAllocatedTrucks());
            availableTrucksRepository.save(availableTrucks);

            booking.setStatus(com.tmc.model.booking.Status.CANCELLED);
            bookingRepository.save(booking);

            Load load = loadRepository.findById(booking.getLoadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Load not found: " + booking.getLoadId()));

            long totalAllocated = bookingRepository.sumAllocatedTrucksForLoad(load.getLoadId());
            if (load.getStatus() == Status.BOOKED && totalAllocated < load.getNoOfTrucks()) {
                load.setStatus(Status.OPEN_FOR_BIDS); // re-open for bids
            }
            // if totalAllocated == 0, you might choose to set POSTED or OPEN_FOR_BIDS; we choose OPEN_FOR_BIDS

            loadRepository.save(load);
            return mapToResponse(booking);

    }
}
