package objectworks.controlador;

import objectworks.modelo.Habitacion;
import objectworks.modelo.HabitacionEstandar;
import objectworks.modelo.HabitacionSuperior;
import objectworks.modelo.Suite;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class HabitacionController {

    private final EntityManager em;

    public HabitacionController() {
        this.em = JPAUtil.getEntityManager();
    }

    public Lista<Habitacion> listarHabitaciones() throws DAOException {
        try {
            TypedQuery<Habitacion> q = em.createQuery("SELECT h FROM Habitacion h ORDER BY h.id", Habitacion.class);
            return new Lista<>(q.getResultList());
        } catch (Exception e) {
            throw new DAOException("Error listando habitaciones", e);
        }
    }

    public Habitacion buscarPorId(int id) throws DAOException {
        return em.find(Habitacion.class, id);
    }

    public Lista<Habitacion> buscarPorHotel(int idHotel) throws DAOException {
        try {
            TypedQuery<Habitacion> q = em.createQuery(
                    "SELECT h FROM Habitacion h WHERE h.hotel.id = :idHotel ORDER BY h.numero", Habitacion.class);
            q.setParameter("idHotel", idHotel);
            return new Lista<>(q.getResultList());
        } catch (Exception e) {
            throw new DAOException("Error buscando por hotel", e);
        }
    }

    public Lista<Habitacion> buscarPorCategoria(CategoriaHabitacion categoria) throws DAOException {
        try {
            TypedQuery<Habitacion> q = em.createQuery(
                    "SELECT h FROM Habitacion h WHERE TYPE(h) = :tipo", Habitacion.class);
            q.setParameter("tipo", claseDe(categoria));
            return new Lista<>(q.getResultList());
        } catch (Exception e) {
            throw new DAOException("Error buscando por categoría", e);
        }
    }

    public Lista<Habitacion> buscarPorHotelCategoria(int idHotel, CategoriaHabitacion categoria) throws DAOException {
        try {
            TypedQuery<Habitacion> q = em.createQuery(
                    "SELECT h FROM Habitacion h WHERE h.hotel.id = :idHotel AND TYPE(h) = :tipo ORDER BY h.numero", Habitacion.class);
            q.setParameter("idHotel", idHotel);
            q.setParameter("tipo", claseDe(categoria));
            return new Lista<>(q.getResultList());
        } catch (Exception e) {
            throw new DAOException("Error buscando por hotel y categoría", e);
        }
    }

    public Lista<Habitacion> buscarPorNumPersonas(int personas) throws DAOException {
        try {
            TypedQuery<Habitacion> q = em.createQuery(
                    "SELECT h FROM Habitacion h WHERE h.capacidad >= :personas ORDER BY h.id", Habitacion.class);
            q.setParameter("personas", personas);
            return new Lista<>(q.getResultList());
        } catch (Exception e) {
            throw new DAOException("Error buscando por personas", e);
        }
    }

    public Lista<Habitacion> buscarPorHotelNumPersonas(int idHotel, int personas) throws DAOException {
        try {
            TypedQuery<Habitacion> q = em.createQuery(
                    "SELECT h FROM Habitacion h WHERE h.hotel.id = :idHotel AND h.capacidad >= :personas ORDER BY h.numero", Habitacion.class);
            q.setParameter("idHotel", idHotel);
            q.setParameter("personas", personas);
            return new Lista<>(q.getResultList());
        } catch (Exception e) {
            throw new DAOException("Error buscando por hotel y personas", e);
        }
    }

    public Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida) throws DAOException {
        return buscarDisponibles(entrada, salida, null);
    }

    public Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida, Integer idReservaIgnorada) throws DAOException {
        try {
            String sql = "SELECT h.* FROM habitacion h WHERE NOT EXISTS (" +
                    "SELECT 1 FROM reserva_habitacion rh JOIN reserva r ON r.id_reserva = rh.id_reserva " +
                    "WHERE rh.id_habitacion = h.id_habitacion AND r.estado <> 'CANCELADA' " +
                    "AND r.fecha_entrada < ? AND r.fecha_salida > ? " +
                    "AND (? IS NULL OR r.id_reserva <> ?))";
            List<Habitacion> result = em.createNativeQuery(sql, Habitacion.class)
                    .setParameter(1, salida)
                    .setParameter(2, entrada)
                    .setParameter(3, idReservaIgnorada)
                    .setParameter(4, idReservaIgnorada)
                    .getResultList();
            return new Lista<>(result);
        } catch (Exception e) {
            throw new DAOException("Error buscando disponibles", e);
        }
    }

    public Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida) throws DAOException {
        return buscarDisponiblesPorHotel(idHotel, entrada, salida, null);
    }

    public Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida, Integer idReservaIgnorada) throws DAOException {
        try {
            String sql = "SELECT h.* FROM habitacion h WHERE h.id_hotel = ? AND NOT EXISTS (" +
                    "SELECT 1 FROM reserva_habitacion rh JOIN reserva r ON r.id_reserva = rh.id_reserva " +
                    "WHERE rh.id_habitacion = h.id_habitacion AND r.estado <> 'CANCELADA' " +
                    "AND r.fecha_entrada < ? AND r.fecha_salida > ? " +
                    "AND (? IS NULL OR r.id_reserva <> ?)) ORDER BY h.numero";
            List<Habitacion> result = em.createNativeQuery(sql, Habitacion.class)
                    .setParameter(1, idHotel)
                    .setParameter(2, salida)
                    .setParameter(3, entrada)
                    .setParameter(4, idReservaIgnorada)
                    .setParameter(5, idReservaIgnorada)
                    .getResultList();
            return new Lista<>(result);
        } catch (Exception e) {
            throw new DAOException("Error buscando disponibles por hotel", e);
        }
    }

    public Lista<Habitacion> habitacionesDisponiblesParaModificar(int idReserva, LocalDate entrada, LocalDate salida) throws Exception {
        return buscarDisponibles(entrada, salida, idReserva);
    }

    private Class<? extends Habitacion> claseDe(CategoriaHabitacion cat) {
        return switch (cat) {
            case ESTANDAR -> HabitacionEstandar.class;
            case SUPERIOR -> HabitacionSuperior.class;
            case SUITE -> Suite.class;
        };
    }
}