package daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import persistence.model.Role;
import persistence.model.User;

import java.util.List;

public class UserDAO extends AbstractDAO
{
    private static UserDAO instance;
    private static EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory emf, Class<User> entityClass)
    {
        super(emf, entityClass);
    }

    public static UserDAO getInstance(EntityManagerFactory _emf)
    {
        if (instance == null)
        {
            emf = _emf;
            instance = new UserDAO(emf, User.class);
        }
        return instance;
    }

    public List<User> getAllUsers()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList();
        }
    }

    public User create(User user){
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            user.addRole(em.find(Role.class, "student"));
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }
}
