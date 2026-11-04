package objectworks.modelo.dao;

import objectworks.modelo.Lista;
import objectworks.modelo.exceptions.DAOException;

/**
 * Operaciones CRUD comunes a todos los DAO.
 *
 * @param <T> clase del modelo (Cliente, Hotel, Habitacion, Reserva...)
 * @param <K> tipo de la clave primaria (Integer, porque son autonumericas)
 */
public interface DAO<T, K> {

    /** Inserta el objeto y devuelve la clave autonumerica generada. */
    K insertar(T objeto) throws DAOException;

    void actualizar(T objeto) throws DAOException;

    void eliminar(K id) throws DAOException;

    /** Devuelve el objeto o null si no existe. */
    T buscarPorId(K id) throws DAOException;

    Lista<T> listarTodos() throws DAOException;
}
