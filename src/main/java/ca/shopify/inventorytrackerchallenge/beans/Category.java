package ca.shopify.inventorytrackerchallenge.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Category.
 *
 * @author Marcin Koziel
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    Long id;
    String title;
    LocalDateTime expiredAt;

}
