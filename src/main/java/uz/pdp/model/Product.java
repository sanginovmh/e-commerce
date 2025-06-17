package uz.pdp.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "product")
public class Product extends BaseModel {
    private String name;
    private Double price;
    private Integer quantity;
    private UUID categoryId;
    private UUID sellerId;
}
