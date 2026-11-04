package objectworks.modelo.dao;

import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.exceptions.DAOException;

/**
 * Operaciones de persistencia de clientes.
 * Ademas del CRUD de DAO, anade las busquedas propias del caso practico.
 */
public interface ClienteDAO extends DAO<Cliente, Integer> {

    /** Devuelve el cliente con ese DNI o null si no existe. */
    Cliente buscarPorDni(String dni) throws DAOException;

    /** Busca clientes cuyo nombre o apellidos contengan el texto. */
    Lista<Cliente> buscarPorNombreOApellido(String texto) throws DAOException;

    /** Indica si el cliente tiene alguna reserva (y por tanto no se puede borrar). */
    boolean tieneReservas(int idCliente) throws DAOException;
}
