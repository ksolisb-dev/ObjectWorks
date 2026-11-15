package objectworks.controlador;

import objectworks.modelo.Reserva;
import objectworks.modelo.Habitacion;
import objectworks.modelo.Hotel;
import objectworks.modelo.Cliente;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.enums.EstadoReserva;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.ReservaNoEncontradaException;

import java.time.LocalDate;

public class ReservaController {

    private final ReservaDAO reservaDAO;
    private final Controlador controladorAntiguo;




    public ReservaController(DAOFactory factory) {
        this.reservaDAO = factory.getReservaDAO();
        this.controladorAntiguo = new Controlador();
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



    // PARTE 5  - creo que esto lo tendras que usar
    public Lista<Reserva> listarTodas() throws DAOException {
        return reservaDAO.listarTodos();
    }

    public Lista<Reserva> listarReservas() throws DAOException {
        return reservaDAO.listarTodos();
    }

    public Lista<Hotel> listarHoteles() {
        return controladorAntiguo.listarHoteles();
    }

    public Lista<Habitacion> getHabitacionesDisponibles(int hotelId, LocalDate entrada, LocalDate salida)
            throws Exception {
        return controladorAntiguo.getHabitacionesDisponibles(hotelId, entrada, salida);
    }

    public Lista<Habitacion> habitacionesDisponiblesParaModificar(int idReserva, LocalDate entrada, LocalDate salida)
            throws Exception {
        Reserva r = buscarReservaPorId(idReserva);
        return controladorAntiguo.getHabitacionesDisponibles(r.getHotel().getId(), entrada, salida);
    }

    public Reserva buscarReservaPorId(int idReserva) throws DAOException, ReservaNoEncontradaException {
        Reserva r = reservaDAO.buscarPorId(idReserva);
        if (r == null) {
            throw new ReservaNoEncontradaException("No existe la reserva con id " + idReserva);
        }
        return r;
    }

    public Reserva crearReserva(String dni, int hotelId, LocalDate entrada, LocalDate salida,
                                int numPersonas, Lista<Habitacion> habitaciones) throws DAOException, Exception {

        Cliente cliente = new Cliente();
        cliente.setDni(dni);

        Hotel hotel = new Hotel(hotelId, "", "", "", 0, "");

        Reserva reserva = new Reserva(
                0, entrada, salida, numPersonas,
                LocalDate.now(), EstadoReserva.CONFIRMADA,
                hotel, cliente, habitaciones
        );

        int id = reservaDAO.insertar(reserva);
        reserva.setId(id);
        return reserva;
    }

    public Reserva modificarReserva(int idReserva, LocalDate entrada, LocalDate salida,
                                    int numPersonas, Lista<Habitacion> habitaciones)
            throws DAOException, ReservaNoEncontradaException {

        Reserva reserva = buscarReservaPorId(idReserva);
        reserva.setFechaEntrada(entrada);
        reserva.setFechaSalida(salida);
        reserva.setNumPersonas(numPersonas);
        reserva.getHabitaciones().clear();
        for (int i = 0; i < habitaciones.size(); i++) {
            reserva.agregarHabitacion(habitaciones.getAt(i));
        }
        reserva.recalcularImporte();

        reservaDAO.actualizar(reserva);
        return reserva;
    }
}