package dao;

import dtos.HealthProductDTO;
import dtos.StorageDTO;
import persistence.model.Product;
import persistence.model.Storage;

import java.util.List;
import java.util.Set;

public interface iDAO<D, T> {

    Set<D> getAll();
    D getById(int id);
    D create(T product);
    D update(D DTO);
    D delete(int id);
}
