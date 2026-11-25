package objectworks;

import objectworks.controlador.ClienteController;
import objectworks.controlador.ConsultasController;
import objectworks.controlador.ReservaController;
import objectworks.controlador.HabitacionController;
import objectworks.controlador.HotelController;
import objectworks.vista.VistaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Un controlador por cada gestion (los demas se anaden en sus partes)
        ClienteController clienteController = new ClienteController();
//        ReservaController reservaController = new ReservaController(factory);
//        HotelController hotelController = new HotelController(factory);
//        HabitacionController habitacionController = new HabitacionController(factory);
//        ConsultasController consultasController = new ConsultasController(factory);

        VistaPrincipal vista = new VistaPrincipal(clienteController, null, null, null, null);
        vista.inicio();
    }
}
