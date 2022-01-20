package ca.shopify.inventorytrackerchallenge.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Brand.
 *
 * @author Marcin Koziel
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    Long id;
    String title;
    String summary;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
