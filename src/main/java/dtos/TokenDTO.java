package dtos;

import lombok.*;
import persistence.model.Role;
import persistence.model.User;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenDTO {
    private String token;
    private String email;
    private Set<String> roles;

    public TokenDTO(String token, User user) {
        this.token = token;
        this.email = user.getEmail();
        this.roles = user.getRolesAsStrings(); // Assuming you have a method to get role names as strings
    }
}
