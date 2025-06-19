package uz.pdp.base;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public abstract class BaseModel {
    private final UUID id;
    @Setter
    private boolean active = true;

    public BaseModel() {
        id = UUID.randomUUID();
    }
}
