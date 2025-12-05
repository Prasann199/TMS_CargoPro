package com.tmc.dto.transporter;

import com.tmc.model.transporter.AvailableTrucks;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
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
public class TransporterResponse {
    private UUID transporterId;
    private String companyName;
    private double rating;
    private List<AvailableTrucks> availableTrucks;

}
