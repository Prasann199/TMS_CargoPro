package com.tmc.dto.bid;

import com.tmc.model.bid.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BidRequest {
    @NotNull(message = "Load id cannot be empty!")
    private UUID loadId;
    @NotNull(message = "Transporter id cannot be empty!")
    private UUID transporterId;
    @NotNull(message = "Proposed rate cannot be empty!")
    @PositiveOrZero
    private double proposedRate;
    @NotNull(message = "trucks offered cannot be empty!")
    @PositiveOrZero
    private int trucksOffered;
    private Status status;
}
