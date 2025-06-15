package uz.pdp.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
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
