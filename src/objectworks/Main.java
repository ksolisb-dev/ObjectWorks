package objectworks;

import objectworks.controlador.ClienteController;
import objectworks.controlador.HotelController;
import objectworks.modelo.dao.DAOFactory;
import objectworks.vista.VistaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Patron Factory: el unico sitio donde se elige el motor de base de datos
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);

        // Un controlador por cada gestion (los demas se anaden en sus partes)
        ClienteController clienteController = new ClienteController(factory);
        HotelController hotelController = new HotelController(factory);

        VistaPrincipal vista = new VistaPrincipal(clienteController, hotelController);
        if (!factory.probarConexion()) {
            vista.mostrarErrorConexion();
            return;
        }
        vista.inicio();
    }
}
