package com.tmc.controller.load;

import com.tmc.dto.booking.ApiResponse;
import com.tmc.dto.load.LoadRequest;
import com.tmc.dto.load.LoadResponse;
import com.tmc.model.bid.Bid;
import com.tmc.model.load.BidWithScore;
import com.tmc.model.load.Load;
import com.tmc.service.load.LoadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/load")
public class LoadController {

    @Autowired
    public LoadService loadService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<LoadResponse>> creadteLoad(@Valid @RequestBody LoadRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(loadService.createLoad(request));
    }

    //I HAVE KEPT THE SIZE TO 1 YOU CAN CHANGE IT TO HOW MUCH IS NEEDED DEFAULT
    @GetMapping("/")
    public ResponseEntity<Page<Load>> getLoads(@RequestParam(required = false) UUID shipperId,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(defaultValue = "0")int page,
                                               @RequestParam(defaultValue = "1")int size){
        Pageable pageable= PageRequest.of(page,size);
        Page<Load> loads=loadService.getLoads(shipperId,status,pageable);
        return ResponseEntity.ok(loads);
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<LoadResponse> getLoad(@PathVariable("loadId")UUID loadId){
        return ResponseEntity.ok(loadService.getLoad(loadId));
    }

    @PatchMapping("/{loadId}/cancel")
    public ResponseEntity<ApiResponse<LoadResponse>> cancelLoad(@PathVariable("loadId") UUID loadId){
        return ResponseEntity.ok(loadService.cancelLoad(loadId));
    }

    @GetMapping("/{loadId}/best-bids")
    public ResponseEntity<List<BidWithScore>> bestBidSuggestions(@PathVariable("loadId") UUID loadId,@RequestParam(defaultValue = "0") int topN){
        return ResponseEntity.ok(loadService.getBestBidsSuggestions(loadId,topN));
    }
}
