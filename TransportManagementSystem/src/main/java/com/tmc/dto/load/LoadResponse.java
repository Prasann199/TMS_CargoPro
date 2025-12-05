package com.tmc.dto.load;

import com.tmc.model.load.Status;
import com.tmc.model.load.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoadResponse {
    private UUID loadId;
    private String shipperId;
    private String loadingCity;
    private String unloadingCity ;
    private LocalDateTime loadingDate;
    private String productType;
    private double weight;
    private WeightUnit weightUnit;
    private String truckType;
    private Integer noOfTrucks;
    private Status status;
    private LocalDateTime datePosted;

}
