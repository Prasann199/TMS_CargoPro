package com.tmc.model.transporter;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Transporter")
public class Transporter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transporterId;
    private String companyName;
    private double rating;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "transporter",orphanRemoval = true)
    private List<AvailableTrucks> availableTrucks;

    public int getTruckCount(String truckType) {
        return availableTrucks.stream()
                .filter(t -> t.getTruckType().equalsIgnoreCase(truckType))
                .map(AvailableTrucks::getCount)
                .findFirst()
                .orElse(0);
    }
}
