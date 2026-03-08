package org.cyndi.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RatingRequest {
    private BigDecimal overallRating;
    private BigDecimal damageRating;
    private BigDecimal survivalRating;
    private BigDecimal utilityRating;
    private String comment;
}
