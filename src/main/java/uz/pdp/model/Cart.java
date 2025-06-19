package uz.pdp.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import uz.pdp.base.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cart extends BaseModel {
    public Cart(UUID customerId) {
        this.customerId = customerId;
    }

    private UUID customerId;
    private List<Item> items = new ArrayList<>();
    private boolean paid;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        UUID productId;
        Integer quantity;
    }
}
