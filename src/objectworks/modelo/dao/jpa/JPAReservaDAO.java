package objectworks.modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.JPAUtil;

import java.util.List;

public class JPAReservaDAO {

    // Importante utilizar el FETCH para cerrar el EntityManager antes de volver el resultado de la consulta.
    private static final String JPQL_BASE =
            "SELECT DISTINCT r FROM Reserva r "
            + "JOIN FETCH r.cliente "
            + "JOIN FETCH r.hotel "
            + "LEFT JOIN FETCH r.habitaciones ";

    private static final String JPQL_POR_ID =
            JPQL_BASE + "WHERE r.id = :id";

    private static final String JPQL_LISTAR =
            JPQL_BASE + "ORDER BY r.fechaEntrada DESC, r.id";

    // METOD0S JPA IMPLEMENTADO

    public Reserva buscarPorId(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException(
                    "El identificador de reserva debe ser positivo"
            );
        }

        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            List<Reserva> resultado = em
                    .createQuery(JPQL_POR_ID, Reserva.class)
                    .setParameter("id", id) // asigna el identificador a :id sin concatenarlo en la consulta
                    .getResultList();

            return resultado.isEmpty() ? null : resultado.getFirst();

        } catch (RuntimeException e) {
            throw new DAOException(
                    "Error al buscar la reserva " + id, e
            );
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Lista<Reserva> listarTodos() throws DAOException {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            List<Reserva> resultado = em
                    .createQuery(JPQL_LISTAR, Reserva.class)
                    .getResultList();

            return new Lista<>(resultado);

        } catch (RuntimeException e) {
            throw new DAOException(
                    "Error al listar las reservas", e
            );
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
