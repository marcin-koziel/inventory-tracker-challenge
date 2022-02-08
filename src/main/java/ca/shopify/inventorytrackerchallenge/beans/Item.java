package ca.shopify.inventorytrackerchallenge.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Item.
 *
 * @author Marcin Koziel
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    public enum Condition {
        BRAND_NEW,
        LIKE_NEW,
        VERY_GOOD,
        GOOD,
        ACCEPTABLE,
        UNACCEPTABLE
    }

    private Long Id;
    private Product product;
    private Brand brand;
    private User supplier;
    private String sku;
    private Float discount;
    private Float price;
    private Float quantity;
    private Condition condition;
    private Integer sold;
    private Integer available;
    private Integer defective;
    private User createdBy;
    private User updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
