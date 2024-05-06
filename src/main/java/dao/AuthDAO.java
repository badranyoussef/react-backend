package daos;

import dtos.UserDTO;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import persistence.model.User;

public class AuthDAO extends AbstractDAO {
    private static EntityManagerFactory emf;
    private static AuthDAO instance;
    public AuthDAO(EntityManagerFactory emf, Class entityClass) {
        super(emf, entityClass);
    }
    public static AuthDAO getInstance(EntityManagerFactory _emf) {
        if(instance == null){
            emf = _emf;
            instance = new AuthDAO(emf, User.class);
        }
        return instance;
    }

    public User verifyUser(String email, String password) {
        try(var em = emf.createEntityManager())
        {
            TypedQuery<User> query = em.createQuery("select u from User u where email = ?1", User.class);
            query.setParameter(1, email);
            User result = query.getSingleResult();
            if(result == null) throw new EntityNotFoundException("No user with that email exist");
            if(!result.verifyPassword(password)) throw new EntityNotFoundException("Wrong password");
            return result;
        }
    }
    public boolean doesUserExist(String email){
        try(var em = emf.createEntityManager()){
            TypedQuery<String> query = em.createQuery("select u.email from User u where email = ?1", String.class);
            query.setParameter(1,email);
            String result = query.getSingleResult();
            if(result == null){
                throw new EntityNotFoundException("No user with that email exist");
            } else{
                return true;
            }
        }
    }
}
