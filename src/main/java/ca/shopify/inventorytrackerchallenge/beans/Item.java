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

    Long Id;
    Product product;
    Brand brand;
    User supplier;
    String sku;
    Float discount;
    Float price;
    Float quantity;
    Condition condition;
    Integer sold;
    Integer available;
    Integer defective;
    User createdBy;
    User updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
