package objectworks.controlador;

import objectworks.modelo.Reserva;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.ReservaDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.ReservaNoEncontradaException;

public class ReservaController {

    private final ReservaDAO reservaDAO;




    public ReservaController(DAOFactory factory) {
        this.reservaDAO = factory.getReservaDAO();
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
}