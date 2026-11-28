package objectworks.modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.JPAUtil;

import java.util.List;

public class JPAClienteDAO implements ClienteDAO {

    private static final String JPQL_LISTAR =
            "SELECT c FROM Cliente c ORDER BY c.apellidos, c.nombre";
    private static final String JPQL_POR_DNI =
            "SELECT c FROM Cliente c WHERE c.dni = :dni";
    private static final String JPQL_POR_NOMBRE =
            "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE :texto OR LOWER(c.apellidos) LIKE :texto " + "ORDER BY c.apellidos, c.nombre";

    //Como reserva aún no es una entidad JPA (Parte 4) se realiza una consulta con SQL Nativo
    private static final String SQL_NUM_RESERVAS =
            "SELECT COUNT(*) FROM reserva WHERE id_cliente = ?1";

    // DML (con transacción)

    @Override
    public Integer insertar(Cliente cliente) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cliente);
            tx.commit();
            return cliente.getId();
        } catch (RuntimeException e) {
            deshacer(tx);
            throw new  DAOException("No se ha podido guardar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(cliente);
            tx.commit();
        }  catch (RuntimeException e) {
            deshacer(tx);
            throw new  DAOException("No se ha podido actualizar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                em.remove(cliente);
            }
            tx.commit();
        } catch (RuntimeException e) {
            deshacer(tx);
            throw new  DAOException("No se ha podido eliminar el cliente", e);
        } finally {
            em.close();
        }
    }

    // CONSULTAS

    @Override
    public Cliente buscarPorId(Integer id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } catch (RuntimeException e) {
            throw new DAOException("No se han podido buscar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Lista<Cliente> listarTodos() throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new Lista<>(em.createQuery(JPQL_LISTAR, Cliente.class).getResultList());
        } catch (RuntimeException e) {
            throw new DAOException("No se ha podido listar los clientes", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Cliente buscarPorDni(String dni) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cliente> resultado = em.createQuery(JPQL_POR_DNI, Cliente.class)
                    .setParameter("dni", dni)
                    .getResultList();
            return (resultado.isEmpty()) ? null : resultado.get(0);
        } catch (RuntimeException e) {
            throw new DAOException("No se han podido buscar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Lista<Cliente> buscarPorNombreOApellido(String texto) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cliente> clientes = em.createQuery(JPQL_POR_NOMBRE, Cliente.class)
                    .setParameter("texto", "%" + texto.toLowerCase() + "%")
                    .getResultList();
            return new Lista<>(clientes);
        } catch (RuntimeException e) {
            throw new DAOException("No se han podido buscar los clientes", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean tieneReservas(int idCliente) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Number total = (Number) em.createNativeQuery(SQL_NUM_RESERVAS)
                    .setParameter(1, idCliente)
                    .getSingleResult();
            return total.longValue() > 0;
        }  catch (RuntimeException e) {
            throw new DAOException("No se ha podido comprobar si el cliente tiene reservas", e);
        } finally {
            em.close();
        }
    }

    private void deshacer(EntityTransaction tx) {
        if (tx.isActive()) {
            tx.rollback();
        }
    }
}
