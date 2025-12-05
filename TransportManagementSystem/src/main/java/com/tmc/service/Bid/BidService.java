package com.tmc.service.Bid;

import com.tmc.dto.bid.BidRequest;
import com.tmc.dto.bid.BidResponse;
import com.tmc.dto.bid.BidSpecifications;
import com.tmc.dto.booking.ApiResponse;
import com.tmc.exception.InsufficientCapacityException;
import com.tmc.exception.InvalidStatusTransitionException;
import com.tmc.exception.ResourceNotFoundException;
import com.tmc.model.bid.Bid;
import com.tmc.model.load.Load;
import com.tmc.model.load.Status;
import com.tmc.model.transporter.Transporter;
import com.tmc.repository.bid.BidRepository;
import com.tmc.repository.load.LoadRepository;
import com.tmc.repository.transporter.AvailableTrucksRepository;
import com.tmc.repository.transporter.TransporterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BidService {

    @Autowired
    public BidRepository bidRepository;
    @Autowired
    public LoadRepository loadRepository;
    @Autowired
    public TransporterRepository transporterRepository;

    @Autowired
    public AvailableTrucksRepository availableTrucksRepository;

    public BidResponse bid(BidRequest request) {
        Load load=loadRepository.findByLoadId(request.getLoadId())
                .orElseThrow(()->{
                    throw new ResourceNotFoundException("Load not found!");
                });

        if(load.getStatus().equals(Status.BOOKED) || load.getStatus().equals(Status.CANCELLED)){
            throw new InvalidStatusTransitionException("Cannot bid on CANCELLED or BOOKED load");
        }
        Transporter transporter=transporterRepository.findByTransporterId(request.getTransporterId())
                .orElseThrow(()-> new ResourceNotFoundException("Transporter not found!"));
        int availableForType = availableTrucksRepository.findByTransporter_TransporterIdAndTruckType(request.getTransporterId(),load.getTruckType()).getCount();
        if (request.getTrucksOffered() > availableForType) {
            throw new InsufficientCapacityException("Not enough trucks of this type!");
        }
        if (load.getStatus().equals(Status.POSTED)) {
            load.setStatus(Status.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }
        Bid bid = new Bid();
        bid.setLoadId(request.getLoadId());
        bid.setTransporterId(request.getTransporterId());
        bid.setProposedRate(request.getProposedRate());
        bid.setTrucksOffered(request.getTrucksOffered());
        bid.setStatus(com.tmc.model.bid.Status.PENDING);
        bid.setSubmittedAt(LocalDateTime.now());

        // 6. Save bid
        Bid savedBid=bidRepository.save(bid);
        return mapToResponse(savedBid);
    }

    public BidResponse getBid(UUID bidId) {

        Bid bid = bidRepository.findByBidId(bidId)
                    .orElseThrow(()-> new ResourceNotFoundException("Bid not found!"));

        return mapToResponse(bid);
    }

    private BidResponse mapToResponse(Bid bid) {
        BidResponse response= BidResponse.builder()
                .proposedRate(bid.getProposedRate())
                .bidId(bid.getBidId())
                .submittedAt(bid.getSubmittedAt())
                .loadId(bid.getLoadId())
                .status(bid.getStatus())
                .transporterId(bid.getTransporterId())
                .trucksOffered(bid.getTrucksOffered())
                .build();
        return response;
    }

    public ApiResponse<BidResponse> rejectBid(UUID bidId) {

            Bid existingBid=bidRepository.findByBidId(bidId)
                    .orElseThrow(()-> new ResourceNotFoundException("Bid not found!"));
            existingBid.setStatus(com.tmc.model.bid.Status.REJECTED);
            Bid bid=bidRepository.save(existingBid);
            ApiResponse<BidResponse> apiResponse= ApiResponse.<BidResponse>builder()
                    .message("Bid rejected successfully!")
                    .data(mapToResponse(bid))
                    .status("UPDATED")
                    .build();
            return apiResponse;

    }

    public Page<Bid> getBids(UUID loadId, UUID transporterId, String status, Pageable pageable) {
        try{
            if(loadId!=null && transporterId!=null && status!=null){
                return bidRepository.findByLoadIdAndTransporterIdAndStatus(loadId,transporterId,status,pageable);
            }
            else if(loadId!=null && transporterId!=null &&(status==null || status.isBlank())){
                return bidRepository.findByLoadIdAndTransporterId(loadId,transporterId,pageable);
            }
            else if(loadId!=null && status!=null && transporterId==null){
                return bidRepository.findByLoadIdAndStatus(loadId,status,pageable);
            }
            else if(transporterId!=null && status!=null && loadId==null){
                return bidRepository.findByTransporterIdAndStatus(transporterId,status,pageable);
            }
            else if(status!=null && loadId==null && transporterId==null){
                return bidRepository.findByStatus(status,pageable);
            } else if (transporterId!=null && loadId==null && (status==null || status.isBlank())) {
                return bidRepository.findByTransporterId(transporterId,pageable);
            }else{
                return bidRepository.findByLoadId(loadId,pageable);
            }
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException("bids not found!");
        }
    }

    public List<Bid> getFilteredBids(UUID loadId, UUID transporterId, String status) {
        try {
            Specification<Bid> spec = BidSpecifications.filterBids(status, loadId, transporterId);
            return bidRepository.findAll(spec);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException("Bids not found!");
        }
    }
}
