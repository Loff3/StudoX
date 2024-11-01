package Model.Dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> get(String id);

    List<T> getAll();

    void save(T t);

    void update(T t, T updatedT);

    void delete(T t);
}
