package com.tmc.controller.bid;

import com.tmc.dto.bid.BidRequest;
import com.tmc.dto.bid.BidResponse;
import com.tmc.dto.booking.ApiResponse;
import com.tmc.model.bid.Bid;
import com.tmc.model.load.Load;
import com.tmc.service.Bid.BidService;
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
@RequestMapping("/bid")
public class BidController {

    @Autowired
    public BidService bidService;

    @PostMapping("/")
    public ResponseEntity<BidResponse> bid(@Valid @RequestBody BidRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(bidService.bid(request));
    }

    //Pagination is enbled to this Filter also if not needed can use below api
    @GetMapping("/paginated")
    public ResponseEntity<Page<Bid>> getBidsByFilterAndPaginated(@RequestParam("loadId")UUID loadId,
                                                             @RequestParam("transporter_id")UUID transporterId,
                                                             @RequestParam("status") String status,
                                                             @RequestParam(defaultValue = "0")int page,
                                                             @RequestParam(defaultValue = "5")int size
                                                             ){

        Pageable pageable= PageRequest.of(page,size);
        Page<Bid> bids=bidService.getBids(loadId,transporterId,status,pageable);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/")
    public ResponseEntity<List<Bid>> getBidsByFilter(@RequestParam(required = false) UUID loadId,
                                                     @RequestParam(required = false) UUID transporterId,
                                                     @RequestParam(required = false) String status){
        return ResponseEntity.ok(bidService.getFilteredBids(loadId,transporterId,status));
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidResponse> getBid(@PathVariable("bidId") UUID bidId){
        return ResponseEntity.ok(bidService.getBid(bidId));
    }
    @PatchMapping("{bidId}/reject")
    public ResponseEntity<ApiResponse<BidResponse>> rejectBid(@PathVariable("bidId")UUID bidId){
        return ResponseEntity.ok(bidService.rejectBid(bidId));
    }
}
