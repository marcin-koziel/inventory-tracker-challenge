package ca.shopify.inventorytrackerchallenge.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Metadata.
 *
 * @author Marcin Koziel
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

    Long id;
    String title;

}
