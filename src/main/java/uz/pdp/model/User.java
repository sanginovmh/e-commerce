package uz.pdp.model;

import lombok.*;
import uz.pdp.base.BaseModel;
import uz.pdp.record.UserInfo;

import java.util.UUID;

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
