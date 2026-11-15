package objectworks.controlador;

import objectworks.modelo.Habitacion;
import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.dao.HabitacionDAO;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DAOException;

import java.time.LocalDate;

public class HabitacionController {
    private final HabitacionDAO habitacionDAO;
    private final ReservaDAO reservaDAO;

    public HabitacionController(DAOFactory factory) {
        this.habitacionDAO = factory.getHabitacionDAO();
        this.reservaDAO = factory.getReservaDAO();
    }

    public Lista<Habitacion> listarHabitaciones() throws DAOException {
        return habitacionDAO.listarTodos();
    }

    public Habitacion buscarPorId(int id) throws DAOException {
        return habitacionDAO.buscarPorId(id);
    }

    public Lista<Habitacion> buscarPorHotel(int idHotel) throws DAOException {
        return habitacionDAO.buscarPorHotel(idHotel);
    }

    public Lista<Habitacion> buscarPorCategoria(CategoriaHabitacion categoria) throws DAOException {
        return habitacionDAO.buscarPorCategoria(categoria);
    }

    public Lista<Habitacion> buscarPorHotelCategoria(int idHotel, CategoriaHabitacion categoria)  throws DAOException {
        return habitacionDAO.buscarPorHotelCategoria(idHotel, categoria);
    }

    public Lista<Habitacion> buscarPorNumPersonas(int personas) throws DAOException {
        return habitacionDAO.buscarPorNumPersonas(personas);
    }

    public Lista<Habitacion> buscarPorHotelNumPersonas(int idHotel, int personas) throws DAOException {
        return habitacionDAO.buscarPorHotelNumPersonas(idHotel, personas);
    }

    public Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida) throws DAOException {
        return habitacionDAO.buscarDisponibles(entrada, salida);
    }

    public Lista<Habitacion> buscarDisponibles(LocalDate entrada, LocalDate salida, Integer idReservaIgnorada) throws DAOException {
        return habitacionDAO.buscarDisponibles(entrada, salida, idReservaIgnorada);
    }

    public Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida) throws DAOException {
        return habitacionDAO.buscarDisponiblesPorHotel(idHotel, entrada, salida);
    }

    public Lista<Habitacion> buscarDisponiblesPorHotel(int idHotel, LocalDate entrada, LocalDate salida, Integer idReservaIgnorada) throws DAOException {
        return habitacionDAO.buscarDisponiblesPorHotel(idHotel, entrada, salida, idReservaIgnorada);
    }

    public Lista<Habitacion> habitacionesDisponiblesParaModificar(int idReserva, LocalDate entrada, LocalDate salida)
            throws Exception {
        Reserva r = reservaDAO.buscarPorId(idReserva);
        return habitacionDAO.buscarDisponiblesPorHotel(r.getHotel().getId(), entrada, salida);
    }
}
