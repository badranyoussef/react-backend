package dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenDTO {
    private String token;
    private String email;
}
