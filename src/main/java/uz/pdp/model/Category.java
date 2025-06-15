package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category extends BaseModel {
    private String name;
    private UUID parentId;
}
