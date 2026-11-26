package objectworks.modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.util.JPAUtil;

import java.util.List;

/**
 * DAO de hoteles con JPA (Parte 2 - Producto 4).
 *
 * Sustituye a MySQLHotelDAO: ya no escribimos SQL ni usamos ResultSet,
 * el EntityManager convierte filas en objetos Hotel (mapeo ORM).
 * Las consultas se escriben en JPQL, que trabaja con clases y atributos
 * (Hotel, h.ciudad) en lugar de tablas y columnas.
 *
 * Cada metodo abre su EntityManager y lo cierra en el finally.
 * Las operaciones que modifican datos van dentro de una transaccion
 * (begin / commit, y rollback si algo falla).
 *
 * Se captura RuntimeException porque JPA lanza excepciones no comprobadas
 * (PersistenceException, IllegalArgumentException si la JPQL es incorrecta...)
 * y las convertimos en DAOException para que la Vista muestre un mensaje.
 */
public class JPAHotelDAO implements HotelDAO {

    private static final String JPQL_LISTAR =
            "SELECT h FROM Hotel h ORDER BY h.nombre";
    private static final String JPQL_POR_CIUDAD =
            "SELECT h FROM Hotel h WHERE LOWER(h.ciudad) LIKE LOWER(:ciudad) ORDER BY h.nombre";
    // JOIN FETCH: trae el hotel y sus habitaciones en una sola consulta (relacion 1:N)
    private static final String JPQL_CON_HABITACIONES =
            "SELECT DISTINCT h FROM Hotel h LEFT JOIN FETCH h.habitaciones WHERE h.id = :id";

    @Override
    public Integer insertar(Hotel hotel) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(hotel);   // INSERT; JPA rellena hotel.id con el AUTO_INCREMENT
            tx.commit();
            return hotel.getId();
        } catch (RuntimeException e) {
            deshacer(tx);
            throw new DAOException("Error al insertar el hotel", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Hotel hotel) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(hotel);     // UPDATE con los datos nuevos del objeto
            tx.commit();
        } catch (RuntimeException e) {
            deshacer(tx);
            throw new DAOException("Error al actualizar el hotel", e);
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
            Hotel hotel = em.find(Hotel.class, id);
            if (hotel != null) {
                em.remove(hotel);  // DELETE
            }
            tx.commit();
        } catch (RuntimeException e) {
            // p. ej. la BD lo impide porque el hotel tiene habitaciones o reservas (ON DELETE RESTRICT)
            deshacer(tx);
            throw new DAOException("No se ha podido eliminar el hotel", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel buscarPorId(Integer id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Hotel.class, id);   // null si no existe
        } catch (RuntimeException e) {
            throw new DAOException("Error al buscar el hotel", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Lista<Hotel> listarTodos() throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Hotel> hoteles = em.createQuery(JPQL_LISTAR, Hotel.class).getResultList();
            return new Lista<>(hoteles);
        } catch (RuntimeException e) {
            throw new DAOException("Error al listar los hoteles", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Lista<Hotel> buscarPorCiudad(String ciudad) throws DAOException, DatoObligatorioException {
        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new DatoObligatorioException("La ciudad es obligatoria");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Hotel> hoteles = em.createQuery(JPQL_POR_CIUDAD, Hotel.class)
                    .setParameter("ciudad", "%" + ciudad.trim() + "%")
                    .getResultList();
            return new Lista<>(hoteles);
        } catch (RuntimeException e) {
            throw new DAOException("Error al buscar hoteles por ciudad", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel buscarConHabitaciones(Integer id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Hotel> resultado = em.createQuery(JPQL_CON_HABITACIONES, Hotel.class)
                    .setParameter("id", id)
                    .getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } catch (RuntimeException e) {
            throw new DAOException("Error al cargar las habitaciones del hotel", e);
        } finally {
            em.close();
        }
    }

    // Deshace la transaccion si se habia quedado abierta
    private void deshacer(EntityTransaction tx) {
        if (tx.isActive()) {
            tx.rollback();
        }
    }
}