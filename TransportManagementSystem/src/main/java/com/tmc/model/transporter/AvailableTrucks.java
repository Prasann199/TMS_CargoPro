package com.tmc.model.transporter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="Available_trucks",uniqueConstraints = @UniqueConstraint(columnNames = {"transporter_transporter_id","truck_type"}))
public class AvailableTrucks {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String truckType;
    private int count;
    @JsonBackReference
    @ManyToOne
    private Transporter transporter;


}
