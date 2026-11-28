package objectworks.controlador;

import objectworks.modelo.Reserva;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Hotel;
import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.ClienteDAO;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.ClienteNoEncontradoException;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.ReservaNoEncontradaException;

import java.time.LocalDate;

public class ReservaController {

    private final ReservaDAO reservaDAO;
    private final ClienteDAO clienteDAO;

    public ReservaController(DAOFactory factory) {
        this.reservaDAO = factory.getReservaDAO();
        this.clienteDAO = factory.getClienteDAO();
    }

    public int crearReserva(Reserva reserva) throws DAOException {
        return reservaDAO.insertar(reserva);
    }

    public void modificarReserva(Reserva reserva) throws DAOException, ReservaNoEncontradaException {
        if (reservaDAO.buscarPorId(reserva.getId()) == null) {
            throw new ReservaNoEncontradaException("No existe la reserva " + reserva.getId());
        }
        reservaDAO.actualizar(reserva);
    }
    public void cancelarReserva(int idReserva) throws DAOException, ReservaNoEncontradaException {
        if (reservaDAO.buscarPorId(idReserva) == null) {
            throw new ReservaNoEncontradaException("No existe la reserva " + idReserva);
        }
        reservaDAO.cancelar(idReserva);
    }

    public Reserva buscarPorId(int id) throws DAOException {
        return reservaDAO.buscarPorId(id);
    }

    public Lista<Reserva> listarTodas() throws DAOException {
        return reservaDAO.listarTodos();
    }

    public Lista<Reserva> listarReservas() throws DAOException {
        return reservaDAO.listarTodos();
    }

    public Reserva buscarReservaPorId(int idReserva) throws DAOException, ReservaNoEncontradaException {
        Reserva r = reservaDAO.buscarPorId(idReserva);
        if (r == null) {
            throw new ReservaNoEncontradaException("No existe la reserva con id " + idReserva);
        }
        return r;
    }

    public Lista<Reserva> buscarReservaPorDni(String dni) throws DAOException, ReservaNoEncontradaException {
        Lista<Reserva> r = reservaDAO.buscarPorDni(dni);
        if (r.isEmpty()) {
            throw new ReservaNoEncontradaException("No existe la reserva con dni " + dni);
        }
        return r;
    }

    public Reserva crearReserva(String dni, int hotelId, LocalDate entrada, LocalDate salida,
                                int numPersonas, Lista<Habitacion> habitaciones) throws DAOException, Exception {

        // Hace falta el cliente real: la reserva se guarda con su id_cliente (FK), no con el DNI
        String dniNormalizado = dni == null ? "" : dni.trim().toUpperCase();
        Cliente cliente = clienteDAO.buscarPorDni(dniNormalizado);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe ningun cliente con DNI " + dniNormalizado);
        }

        Hotel hotel = new Hotel(hotelId, "", "", "", 0, "");

        Reserva reserva = new Reserva(
                0, entrada, salida, numPersonas,
                LocalDate.now(), EstadoReserva.CONFIRMADA,
                hotel, cliente, habitaciones
        );

        int id = reservaDAO.insertar(reserva);
        return buscarReservaPorId(id);
    }

    public Reserva modificarReserva(int idReserva, LocalDate entrada, LocalDate salida,
                                    int numPersonas, Lista<Habitacion> habitaciones)
            throws DAOException, ReservaNoEncontradaException {

        Reserva reserva = buscarReservaPorId(idReserva);
        reserva.setFechaEntrada(entrada);
        reserva.setFechaSalida(salida);
        reserva.setNumPersonas(numPersonas);
        reserva.setHabitaciones(habitaciones); // Implementado para ORM

        reservaDAO.actualizar(reserva);
        return buscarReservaPorId(idReserva);
    }
}