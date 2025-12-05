package com.tmc.controller.transporter;

import com.tmc.dto.booking.ApiResponse;
import com.tmc.dto.transporter.TransporterRequest;
import com.tmc.dto.transporter.TransporterResponse;
import com.tmc.dto.transporter.TruckUpdateRequest;
import com.tmc.model.transporter.AvailableTrucks;
import com.tmc.service.transporter.TransporterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transporter")
public class TransporterController {

    @Autowired
    public TransporterService transporterService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<TransporterResponse>> createTransporter(@Valid @RequestBody TransporterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transporterService.createTransporter(request));
    }

    @GetMapping("/{transporterId}")
    public ResponseEntity<TransporterResponse> getTransporter(@PathVariable("transporterId")UUID transporterId){
        return ResponseEntity.ok(transporterService.getTransporter(transporterId));
    }

    @PutMapping("/{transporterId}/trucks")
    public ResponseEntity<ApiResponse<TransporterResponse>> updateAvailableTrucks(@PathVariable("transporterId")UUID transporterId, @RequestBody List<TruckUpdateRequest> truckUpdates){
        return ResponseEntity.ok(transporterService.updateAvailableTrucks(transporterId,truckUpdates));
    }

}
