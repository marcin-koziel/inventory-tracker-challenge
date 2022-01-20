package ca.shopify.inventorytrackerchallenge.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Product.
 *
 * @author Marcin Koziel
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    Long id;
    String name;
    String summary;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<Category> categoryList;

}
