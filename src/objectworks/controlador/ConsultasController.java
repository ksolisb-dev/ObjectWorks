package objectworks.controlador;

import objectworks.modelo.Lista;
import objectworks.modelo.Reserva;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

import java.time.LocalDate;

public class ConsultasController {

    private final ReservaDAO reservaDAO;
    private final HotelDAO hotelDAO;

    public ConsultasController(DAOFactory factory) {
        this.reservaDAO = factory.getReservaDAO();
        this.hotelDAO = factory.getHotelDAO();
    }

    public Lista<Reserva> listarTodasLasReservas() throws DAOException {
        return reservaDAO.listarTodos();
    }

    public Lista<Reserva> buscarReservasPorCliente(String dni)
            throws DatoObligatorioException, DAOException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new DatoObligatorioException("El DNI del cliente es obligatorio");
        }
        String dniNormalizado = dni.trim().toUpperCase();

        return reservaDAO.buscarPorDni(dniNormalizado);
    }

    public Lista<Reserva> buscarReservasPorHotel(int hotelId) throws HotelNoEncontradoException, DAOException {
        if (hotelDAO.buscarPorId(hotelId) == null) {
            throw new HotelNoEncontradoException("No existe el hotel con ID " + hotelId);
        }
        return reservaDAO.listarPorHotel(hotelId);
    }

    public Lista<Reserva> buscarReservasPorFechaEntrada(LocalDate fecha)
            throws DatoObligatorioException, DAOException {
        comprobarFecha(fecha);
        return reservaDAO.listarPorFechaEntrada(fecha);
    }

    public Lista<Reserva> buscarReservasPorFechaSalida(LocalDate fecha)
            throws DatoObligatorioException, DAOException {
        comprobarFecha(fecha);
        return reservaDAO.listarPorFechaSalida(fecha);
    }

    public Lista<Reserva> buscarReservasPorEstado(EstadoReserva estado)
            throws DatoObligatorioException, DAOException {
        if (estado == null) {
            throw new DatoObligatorioException("El estado de la reserva es obligatorio");
        }
        return reservaDAO.listarPorEstado(estado);
    }

    // ===================== AUXILIARES =====================

    private void comprobarFecha(LocalDate fecha) throws DatoObligatorioException {
        if (fecha == null) {
            throw new DatoObligatorioException("La fecha es obligatoria");
        }
    }
}
