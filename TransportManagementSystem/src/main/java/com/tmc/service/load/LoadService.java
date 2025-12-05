package com.tmc.service.load;

import com.tmc.dto.booking.ApiResponse;
import com.tmc.dto.load.LoadRequest;
import com.tmc.dto.load.LoadResponse;
import com.tmc.exception.BadRequestException;
import com.tmc.exception.GlobalExceptionHandler;
import com.tmc.exception.MethodArgumentNotValidException;
import com.tmc.exception.ResourceNotFoundException;
import com.tmc.model.bid.Bid;
import com.tmc.model.load.BidWithScore;
import com.tmc.model.load.Load;
import com.tmc.model.load.Status;
import com.tmc.model.transporter.Transporter;
import com.tmc.repository.bid.BidRepository;
import com.tmc.repository.load.LoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoadService {

    @Autowired
    public LoadRepository loadRepository;

    @Autowired
    public BidRepository bidRepository;

    public ApiResponse<LoadResponse> createLoad(LoadRequest request) {

            if(request.getLoadingDate().isBefore(LocalDateTime.now())){
                throw new BadRequestException("Loading date is already passed!");
            }
            Load createdLoad= Load.builder()
                    .loadingCity(request.getLoadingCity())
                    .shipperId(request.getShipperId())
                    .loadingDate(request.getLoadingDate())
                    .loadingDate(request.getLoadingDate())
                    .status(Status.POSTED)
                    .truckType(request.getTruckType())
                    .weight(request.getWeight())
                    .noOfTrucks(request.getNoOfTrucks())
                    .weightUnit(request.getWeightUnit())
                    .productType(request.getProductType())
                    .loadingCity(request.getLoadingCity())
                    .unloadingCity(request.getUnloadingCity())
                    .build();

            Load savedLoad=loadRepository.save(createdLoad);
            LoadResponse response=mapToResponse(savedLoad);

            //creating map to show both created message and which object is added to DB
            ApiResponse<LoadResponse> apiResponse= ApiResponse.<LoadResponse>builder()
                    .message("Load created Successfully!")
                    .data(response)
                    .status("CREATED")
                    .build();
            return apiResponse;

    }

    private LoadResponse mapToResponse(Load savedLoad) {

         return LoadResponse.builder()
                .loadId(savedLoad.getLoadId())
                .status(savedLoad.getStatus())
                .shipperId(savedLoad.getShipperId())
                .loadingCity(savedLoad.getLoadingCity())
                .weight(savedLoad.getWeight())
                .unloadingCity(savedLoad.getUnloadingCity())
                .weightUnit(savedLoad.getWeightUnit())
                .loadingDate(savedLoad.getLoadingDate())
                .datePosted(savedLoad.getDatePosted())
                .noOfTrucks(savedLoad.getNoOfTrucks())
                .productType(savedLoad.getProductType())
                .truckType(savedLoad.getTruckType())
                .build();
    }

    public LoadResponse getLoad(UUID loadId) {

            Load load=loadRepository.findByLoadId(loadId)
                    .orElseThrow(()->  new ResourceNotFoundException("Load not found!"));
            return mapToResponse(load);

    }

    public ApiResponse<LoadResponse> cancelLoad(UUID loadId) {

        Map<String,Object> response=new HashMap<>();
        ApiResponse<LoadResponse> apiResponse=new ApiResponse<>();

            Load existingLoad=loadRepository.findByLoadId(loadId)
                    .orElseThrow(()-> new ResourceNotFoundException("Load not found!"));
            if(!existingLoad.getStatus().equals(Status.BOOKED) && !existingLoad.getStatus().equals(Status.CANCELLED) ){
                existingLoad.setStatus(Status.CANCELLED);
                Load updatedLoad=loadRepository.save(existingLoad);
                apiResponse= ApiResponse.<LoadResponse>builder()
                        .message("Load cancelled successfully!")
                        .data(mapToResponse(updatedLoad))
                        .status("UPDATED")
                        .build();
            } else if (existingLoad.getStatus().equals(Status.CANCELLED)) {
                apiResponse= ApiResponse.<LoadResponse>builder()
                        .message("Load already in cancelled state!")
                        .data(mapToResponse(existingLoad))
                        .status("FAILED")
                        .build();
            } else{
                apiResponse= ApiResponse.<LoadResponse>builder()
                        .message("Cannot cancel load that's already BOOKED")
                        .data(mapToResponse(existingLoad))
                        .status("FAILED")
                        .build();
            }

        return apiResponse;
    }


    public Page<Load> getLoads(UUID shipperId, String status, Pageable pageable) {

            if(shipperId!=null && status!=null){
                return loadRepository.findByShipperIdAndStatus(shipperId,status,pageable);
            }else if(shipperId!=null){
                return loadRepository.findByShipperId(shipperId,pageable);
            } else if (status!=null) {
                return loadRepository.findByStatus(status,pageable);
            }else{
                return loadRepository.findAll(pageable);
            }

    }

    public List<BidWithScore> getBestBidsSuggestions(UUID loadId,int topN) {

            List<Object[]> activeBidsWithTransporters = bidRepository.findActiveBidsWithTransporterByLoadId(loadId);
            if(activeBidsWithTransporters==null){
                throw new ResourceNotFoundException("Active bids with transporter not found!");
            }
            List<BidWithScore> bidsWithScores = activeBidsWithTransporters.stream()
                    .map(arr -> {
                        Bid bid = (Bid) arr[0];
                        Transporter transporter = (Transporter) arr[1];
                        double proposedRate = bid.getProposedRate();
                        if (proposedRate <= 0) {
                            throw new BadRequestException("Proposed rate must be greater than 0 for bid: " + bid.getBidId());
                        }
                        double rating = transporter.getRating();
                        double score = (1.0 / proposedRate) * 0.7 + (rating / 5.0) * 0.3;
                        return new BidWithScore(bid.getBidId(), bid.getTransporterId(), proposedRate, rating, score);
                    })
                    .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))  // Descending order
                    .collect(Collectors.toList());
            if(bidsWithScores==null || bidsWithScores.size()<1){
                throw  new ResourceNotFoundException("Best-bids not found!");
            }

            return topN > 0 ? bidsWithScores.stream().limit(topN).collect(Collectors.toList()) : bidsWithScores;


    }
}
