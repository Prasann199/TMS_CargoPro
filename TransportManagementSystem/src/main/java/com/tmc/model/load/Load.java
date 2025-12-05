package com.tmc.model.load;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loads")
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @CreationTimestamp
    private LocalDateTime datePosted;
    @Version
    private Long version;
}
