package objectworks.modelo.dao;

import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DAOException;

import java.time.LocalDate;


/**
 * Operaciones de persistencia de habitaciones.
 * Ademas del CRUD de DAO, anade las busquedas propias del caso practico.
 *
 */
public interface HabitacionDAO extends  DAO<Habitacion, Integer> {

    /** Devuelve las habitaciones de ese hotel o lista vacía si no tiene. */
    Lista<Habitacion> buscarPorHotel(int id_hotel) throws DAOException;

    /** Busca habitaciones por categoria. */
    Lista<Habitacion> buscarPorCategoria(CategoriaHabitacion categoria) throws DAOException;

    /** Busca habitaciones por hotel y por categoria. */
    Lista<Habitacion> buscarPorHotelCategoria(int id_hotel, CategoriaHabitacion categoria) throws DAOException;

    /** Busca habitaciones por número de personas. */
    Lista<Habitacion> buscarPorNumPersonas(int personas) throws DAOException;

    /** Busca habitaciones por hotel y por número de personas. */
    Lista<Habitacion> buscarPorHotelNumPersonas(int id_hotel, int personas) throws DAOException;

    /** Busca habitaciones disponibles entre 2 fechas. */
    Lista<Habitacion> buscarDisponibles(LocalDate fechaInicio, LocalDate fechaFin) throws DAOException;

    /** Busca habitaciones disponibles por hotel entre 2 fechas. */
    Lista<Habitacion> buscarDisponiblesPorHotel(int id_hotel, LocalDate fechaEntrada, LocalDate fechaSalida) throws DAOException;

    /** Excluyen la reserva indicada al consultar disponibilidad por fechas, para cuando hagamos las modificaciones de habitaciones. */
    Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida, Integer idReservaIgnorada) throws DAOException;
    Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida, Integer idReservaIgnorada) throws DAOException;

}
