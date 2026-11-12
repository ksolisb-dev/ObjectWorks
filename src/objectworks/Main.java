package objectworks;

import objectworks.controlador.ClienteController;
import objectworks.controlador.ReservaController;
import objectworks.modelo.dao.DAOFactory;
import objectworks.vista.VistaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Patron Factory: el unico sitio donde se elige el motor de base de datos
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);

        // Un controlador por cada gestion (los demas se anaden en sus partes)
        ClienteController clienteController = new ClienteController(factory);
        ReservaController reservaController = new ReservaController(factory);

        VistaPrincipal vista = new VistaPrincipal(clienteController, reservaController);
        if (!factory.probarConexion()) {
            vista.mostrarErrorConexion();
            return;
        }
        vista.inicio();
    }
}
