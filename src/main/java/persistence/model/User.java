package persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int phone;

    @Column(nullable = false, columnDefinition = "student")
    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "rolename", referencedColumnName = "rolename"))
    private Set<Role> roles = new HashSet<>();

    public User(String name, String email, String password, int phone) {
        this.name = name;
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.phone = phone;
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    @JsonIgnore
    //Bi-directional
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "registrations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();

    public void addEvent(Event event) {
        if (event != null) {
            events.add(event);
            event.getUsers().add(this);
        }
    }

    public void removeEvent(Event event) {
        if (event != null) {
            events.remove(event);
            event.getUsers().remove(this);
        }
    }

    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
            role.getUsers().add(this);
        }
    }

    public Set<String> getRolesAsStrings() {
        Set<String> roleStringSet = new HashSet<>();
        this.getRoles().forEach(role -> roleStringSet.add(role.getRolename()));
        return roleStringSet;
    }

    @Override
    public String toString() {
        return "User - " +
                "id = " + id +
                ", name = " + name +
                ", email = " + email +
                ", password = " + password +
                ", phone = " + phone;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
