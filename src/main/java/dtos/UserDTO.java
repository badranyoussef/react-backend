package dtos;

import lombok.*;
import persistence.model.User;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO
{
    private int id;
    private String name;
    private String email;
    private String password;
    private int phone;
    private Set<String> roles;


    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDTO(User user)
    {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.roles = user.getRolesAsStrings();
    }

    public UserDTO(int id, String name, String email, int phone, Set<String> roles)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roles = roles;
    }
}
