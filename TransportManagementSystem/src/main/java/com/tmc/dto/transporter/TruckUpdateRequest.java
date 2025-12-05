package com.tmc.dto.transporter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TruckUpdateRequest {
    @NotNull(message = "truck type cannot be empty!")
    @NotBlank(message = "truck type cannot be empty!")
    private String truckType;
    @NotNull(message = "truck type cannot be empty!")
    @PositiveOrZero
    private int count;
}
