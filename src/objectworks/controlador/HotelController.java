package objectworks.controlador;

import objectworks.modelo.Hotel;
import objectworks.modelo.Lista;
import objectworks.modelo.Validador;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.dao.HotelDAO;
import objectworks.modelo.exceptions.DAOException;
import objectworks.modelo.exceptions.DatoInvalidoException;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.HotelConHabitacionesException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

/**
 * Controlador de la gestion de hoteles (Parte 2 - Producto 4).
 *
 * Hace de puente entre GestionHotelesVista y el Modelo:
 * valida los datos con Validador y guarda/lee con el HotelDAO.
 * No sabe si los datos se guardan con JDBC o con JPA:
 * recibe el DAO de la DAOFactory (en Main se elige DAOFactory.JPA).
 * No imprime nada ni lee teclado (eso es solo de la Vista).
 */
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

    /** Devuelve el hotel con sus habitaciones (usa la relacion 1:N Hotel - Habitacion). */
    public Hotel verHotelConHabitaciones(int id) throws DAOException, HotelNoEncontradoException {
        Hotel h = hotelDAO.buscarConHabitaciones(id);
        if (h == null) {
            throw new HotelNoEncontradoException("No existe el hotel con ID " + id);
        }
        return h;
    }

    /** Da de alta un hotel y devuelve el id que le ha asignado la BD. */
    public int altaHotel(String nombre, String ciudad, String direccion, int estrellas, String telefono)
            throws DatoInvalidoException, DAOException {
        Validador.validarDatosHotel(nombre, ciudad, direccion, estrellas, telefono);
        Hotel hotel = new Hotel(nombre.trim(), ciudad.trim(), direccion.trim(), estrellas, limpiar(telefono));
        return hotelDAO.insertar(hotel);
    }

    public void modificarHotel(int id, String nombre, String ciudad, String direccion, int estrellas, String telefono)
            throws DatoInvalidoException, DAOException, HotelNoEncontradoException {
        Hotel hotel = verHotel(id);   // comprueba que existe
        Validador.validarDatosHotel(nombre, ciudad, direccion, estrellas, telefono);
        hotel.setNombre(nombre.trim());
        hotel.setCiudad(ciudad.trim());
        hotel.setDireccion(direccion.trim());
        hotel.setEstrellas(estrellas);
        hotel.setTelefono(limpiar(telefono));
        hotelDAO.actualizar(hotel);
    }

    /** Solo se puede eliminar un hotel que no tenga habitaciones (la BD tambien lo impide). */
    public void eliminarHotel(int id)
            throws DAOException, HotelNoEncontradoException, HotelConHabitacionesException {
        Hotel hotel = verHotelConHabitaciones(id);
        int numHabitaciones = hotel.getHabitaciones().size();
        if (numHabitaciones > 0) {
            throw new HotelConHabitacionesException("El hotel '" + hotel.getNombre() + "' tiene "
                    + numHabitaciones + " habitaciones. Eliminalas antes de borrar el hotel.");
        }
        hotelDAO.eliminar(id);
    }

    // El telefono es opcional: si viene vacio se guarda NULL
    private String limpiar(String telefono) {
        return (telefono == null || telefono.isBlank()) ? null : telefono.trim();
    }
}