package objectworks.controlador;

import objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

public class HotelController {
    private final HotelDAO hotelDAO;

    public HotelController(DAOFactory factory) {
        this.hotelDAO = factory.getHotelDAO();
    }

    public Lista<Hotel> listarHoteles() throws DAOException {
        return hotelDAO.listarTodos();
    }

    public Lista<Hotel> buscarHotelesPorCiudad(String ciudad) throws DatoObligatorioException, DAOException {
        return hotelDAO.buscarPorCiudad(ciudad);
    }

    public Hotel verHotel(int id) throws DAOException, HotelNoEncontradoException {
        Hotel h = hotelDAO.buscarPorId(id);
        if (h == null) {
            throw new HotelNoEncontradoException("No existe el hotel con ID " + id);
        }
        return h;
    }
}
