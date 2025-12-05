package com.tmc.dto.bid;

import com.tmc.model.bid.Bid;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;

public class BidSpecifications {

    public static Specification<Bid> filterBids(String status, UUID loadId,UUID transporterId){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates=new ArrayList<>();
            if(status!=null && !status.isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("status"),status));
            }
            if(loadId!=null){
                predicates.add(criteriaBuilder.equal(root.get("loadId"),loadId));
            }
            if(transporterId!=null){
                predicates.add(criteriaBuilder.equal(root.get("transporterId"),transporterId));
            }
            return criteriaBuilder.and(predicates.toArray(predicates.toArray(new Predicate[0])));
        });
    }
}
