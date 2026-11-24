package objectworks;

import objectworks.controlador.ClienteController;
import objectworks.controlador.ConsultasController;
import objectworks.controlador.ReservaController;
import objectworks.controlador.HabitacionController;
import objectworks.controlador.HotelController;
import objectworks.modelo.dao.DAOFactory;
import objectworks.vista.VistaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Patron Factory: el unico sitio donde se elige como se guardan los datos
        // Producto 4: JPA (antes DAOFactory.MYSQL con JDBC)
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.JPA);

        // Un controlador por cada gestion (los demas se anaden en sus partes)
        ClienteController clienteController = new ClienteController(factory);
        ReservaController reservaController = new ReservaController(factory);
        HotelController hotelController = new HotelController(factory);
        HabitacionController habitacionController = new HabitacionController(factory);
        ConsultasController consultasController = new ConsultasController(factory);

        VistaPrincipal vista = new VistaPrincipal(clienteController, reservaController, hotelController,
                habitacionController, consultasController);
        if (!factory.probarConexion()) {
            vista.mostrarErrorConexion();
            factory.cerrar();
            return;
        }
        vista.inicio();
        factory.cerrar();
    }
}