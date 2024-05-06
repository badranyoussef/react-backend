package dao;

import java.util.Set;

public interface iDAO <D, T> {

    Set<D> getAll();
    D getById(int id);
    D create(T product);
    D update(D DTO);
    D delete(int id);
}
