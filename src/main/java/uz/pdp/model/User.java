package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel {
    public enum UserRole {
        CUSTOMER,
        SELLER,
        ADMIN,
    }

    private String fullName;
    private String username;
    private String password;
    private UserRole role;
}
