package objectworks;

import objectworks.controlador.ClienteController;
import objectworks.controlador.ConsultasController;
import objectworks.controlador.ReservaController;
import objectworks.controlador.HabitacionController;
import objectworks.controlador.HotelController;
import objectworks.modelo.dao.DAOFactory;
import objectworks.modelo.util.JPAUtil;
import objectworks.vista.VistaPrincipal;

import objectworks.modelo.dao.DAOFactory;

public class Main {
    public static void main(String[] args) {
        //Clientes ya usa JPA
        //Hoteles, habitaciones, reservas y consultas siguen con JDBC (DAO + Factory)
        // hasta que cada parte migre; cuando todas lo estén, la factory desaparecerá
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);

        ClienteController clienteController = new ClienteController();
        ReservaController reservaController = new ReservaController(factory);
        HotelController hotelController = new HotelController(factory);
        HabitacionController habitacionController = new HabitacionController(factory);
        ConsultasController consultasController = new ConsultasController(factory);

        VistaPrincipal vista = new VistaPrincipal(clienteController, reservaController, hotelController, habitacionController, consultasController);

        //Se comprueba la conexión al arrancar, antes de mostrar el menú
        if (!JPAUtil.inicializar() || !factory.probarConexion()) {
            vista.mostrarErrorConexion();
            return;
        }

        try {
            vista.inicio();
        } finally {
            //Se cierra Hibernate y su pool de conexiones al salir
            JPAUtil.close();
        }
    }
}
