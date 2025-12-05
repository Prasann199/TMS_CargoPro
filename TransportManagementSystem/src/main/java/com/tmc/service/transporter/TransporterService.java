package com.tmc.service.transporter;

import com.tmc.dto.booking.ApiResponse;
import com.tmc.dto.transporter.TransporterRequest;
import com.tmc.dto.transporter.TransporterResponse;
import com.tmc.dto.transporter.TruckUpdateRequest;
import com.tmc.exception.ResourceNotFoundException;
import com.tmc.model.transporter.AvailableTrucks;
import com.tmc.model.transporter.Transporter;
import com.tmc.repository.transporter.AvailableTrucksRepository;
import com.tmc.repository.transporter.TransporterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransporterService {

    @Autowired
    public TransporterRepository transporterRepository;

    @Autowired
    private AvailableTrucksRepository trucksRepo;


    public ApiResponse<TransporterResponse> createTransporter(TransporterRequest request) {
            Transporter newTransporter = Transporter.builder()
                    .companyName(request.getCompanyName())
                    .rating(request.getRating())
                    .build();

            List<AvailableTrucks> trucks = request.getAvailableTrucks();

            if (trucks != null) {
                trucks.forEach(truck -> truck.setTransporter(newTransporter));
                newTransporter.setAvailableTrucks(trucks);
            }

            Transporter savedTransporter = transporterRepository.save(newTransporter);

            TransporterResponse transporterResponse = mapToResponse(savedTransporter);

            ApiResponse<TransporterResponse> apiResponse = ApiResponse.<TransporterResponse>builder()
                    .message("transporter created successfully!")
                    .data(transporterResponse)
                    .status("CREATED")
                    .build();

            return apiResponse;

    }


    private TransporterResponse mapToResponse(Transporter savedTransporter) {
        TransporterResponse transporterResponse= TransporterResponse.builder()
                .transporterId(savedTransporter.getTransporterId())
                .companyName(savedTransporter.getCompanyName())
                .rating(savedTransporter.getRating())
                .availableTrucks(savedTransporter.getAvailableTrucks())
                .build();
        return transporterResponse;
    }

    public TransporterResponse getTransporter(UUID transporterId) {
        Transporter transporter = transporterRepository.findByTransporterId(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found!"));
        TransporterResponse response=mapToResponse(transporter);
            response.setAvailableTrucks(trucksRepo.findByTransporter_TransporterId(transporterId));
            return response;

    }

    public ApiResponse<TransporterResponse> updateAvailableTrucks(UUID transporterId, List<TruckUpdateRequest> truckUpdates) {


            Transporter transporter=transporterRepository.findByTransporterId(transporterId)
                    .orElseThrow(()-> new ResourceNotFoundException("Transporter not found!"));
            for(TruckUpdateRequest truck: truckUpdates){
                AvailableTrucks existingTrucks=trucksRepo.findByTransporter_TransporterIdAndTruckType(transporterId,truck.getTruckType());
//
                if(existingTrucks!=null){
                    existingTrucks.setCount(existingTrucks.getCount()+truck.getCount());
                    trucksRepo.save(existingTrucks);
                }else{
                    AvailableTrucks trucks= AvailableTrucks.builder()
                            .truckType(truck.getTruckType())
                            .count(truck.getCount())
                            .transporter(transporter)
                            .build();
                    trucksRepo.save(trucks);
                }

            }

            ApiResponse<TransporterResponse> apiResponse= ApiResponse.<TransporterResponse>builder()
                    .message("Available trucks updated successfully!")
                    .data(getTransporter(transporterId))
                    .status("UPDATED")
                    .build();

            return apiResponse;

    }
}
