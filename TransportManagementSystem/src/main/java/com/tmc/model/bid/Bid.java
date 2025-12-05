package com.tmc.model.bid;

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
@Table(name = "Bid")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bidId;
    private UUID loadId;
    private UUID transporterId;
    private double proposedRate;
    private int trucksOffered;
    private Status status;
    @CreationTimestamp
    private LocalDateTime submittedAt;
    @Version
    private Long version;

}
