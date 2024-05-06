package dao;

import exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import persistence.config.HibernateConfig;
import persistence.model.Car;
import persistence.model.Role;
import persistence.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDAO implements iDAO<User, User> {
    private static UserDAO instance;
    private static EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }

    public static UserDAO getInstance(EntityManagerFactory _emf)
    {
        if (instance == null)
        {
            emf = _emf;
            instance = new UserDAO(emf);
        }
        return instance;
    }


    @Override
    public Set<User> getAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList().stream().collect(Collectors.toSet());
        }
    }

    @Override
    public User getById(int id) {
        User user = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            user = em.find(User.class, id);
            em.getTransaction().commit();
            return user;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "User not found. User is null", e.getTimeStamp());
        }
    }

    @Override
    public User create(User user){
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            user.addRole(em.find(Role.class, "admin"));
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User update(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
        }
    }

    @Override
    public User delete(int id) {
        return null;
    }
}
