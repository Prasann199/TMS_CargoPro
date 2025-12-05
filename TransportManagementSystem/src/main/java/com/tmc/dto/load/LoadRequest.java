package com.tmc.dto.load;

import com.tmc.model.load.Status;
import com.tmc.model.load.WeightUnit;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoadRequest {

    @NotNull(message = "shipper id cannot be empty!")
    @NotBlank(message = "shipper id cannot be empty!")
    private String shipperId;
    @NotNull(message = "Loading city cannot be empty!")
    @NotBlank(message = "Loading city cannot be empty!")
    private String loadingCity;
    @NotNull(message = "Unloading city cannot be empty!")
    @NotBlank(message = "Unloading city cannot be empty!")
    private String unloadingCity ;
    @NotNull(message = "loading date time cannot be empty!")
    private LocalDateTime loadingDate;
    private String productType;
    @NotNull(message = "weight cannot be empty!")
    @PositiveOrZero
    private double weight;
    @NotNull(message = "WeightUnit cannot be empty!")
    private WeightUnit weightUnit;
    @NotNull(message = "truckType cannot be empty!")
    @NotBlank(message = "truckType cannot be empty!")
    private String truckType;
    @PositiveOrZero
    @NotNull(message = "number of trucks cannot be empty!")
    private Integer noOfTrucks;
    private Status status;
}
