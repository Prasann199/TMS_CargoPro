package com.tmc.model.load;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidWithScore {
    private UUID bidId;
    private UUID transporterId;
    private double proposedRate;
    private double rating;
    private double score;
}
