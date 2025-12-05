package com.tmc.dto.transporter;

import com.tmc.model.transporter.AvailableTrucks;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransporterRequest {
    @NotNull(message = "Company name cannot be empty!")
    @NotBlank(message = "Company name cannot be empty!")
    private String companyName;
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    private double rating;

    private List<AvailableTrucks> availableTrucks;
}
