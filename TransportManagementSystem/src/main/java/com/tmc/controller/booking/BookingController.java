package com.tmc.controller.booking;

import com.tmc.dto.booking.ApiResponse;
import com.tmc.dto.booking.BookingRequest;
import com.tmc.dto.booking.BookingResponse;
import com.tmc.service.booking.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    public BookingService bookingService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request){
        try {
            ApiResponse<BookingResponse> response = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Conflict")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);  // 409 for concurrency/duplicate
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // 400 for validation errors
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable("bookingId")UUID bookingId){
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable("bookingId")UUID bookingId){
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }
}
