package uz.pdp.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

import java.util.UUID;
@JacksonXmlRootElement(localName = "product")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product extends BaseModel {
    private String name;
    private Double price;
    private Integer quantity;
    private UUID categoryId;
    private UUID sellerId;

    public void setPrice(Double price) throws IllegalArgumentException {
        if (price > 0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Price must be positive.");
        }
    }

    public void setQuantity(Integer quantity) throws IllegalArgumentException {
        if (quantity > 0) {
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
    }
}
