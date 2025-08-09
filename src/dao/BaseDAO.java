// Base DAO interface for common CRUD operations
package dao;

import java.util.List;
import java.util.Optional;

import exception.DataAccessException;

public interface BaseDAO<T, ID> {
    T save(T entity) throws DataAccessException;
    T update(T entity) throws DataAccessException;
    boolean delete(ID id) throws DataAccessException;
    Optional<T> findById(ID id) throws DataAccessException;
    List<T> findAll() throws DataAccessException;
    boolean exists(ID id) throws DataAccessException;
}
