package dtos;

import lombok.*;
import persistence.model.Role;

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
}
