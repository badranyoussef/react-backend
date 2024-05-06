package dao;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import persistence.config.HibernateConfig;
import persistence.model.User;

public class AuthDAO {
    private static EntityManagerFactory emf;
    private static AuthDAO instance;


    public AuthDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }

    public static AuthDAO getInstance(EntityManagerFactory _emf) {
        if(instance == null){
            emf = _emf;
            instance = new AuthDAO(emf);
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
