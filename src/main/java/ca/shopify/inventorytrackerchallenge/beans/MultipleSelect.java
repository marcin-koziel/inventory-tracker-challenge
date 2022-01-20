package ca.shopify.inventorytrackerchallenge.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The type MultipleSelect.
 *
 * @author Marcin Koziel
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleSelect<T> {

    List<T> populatedObjectList;
    List<String> selectedIdList;

}

