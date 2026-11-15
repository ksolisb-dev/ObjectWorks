package objectworks.vista;

import java.util.Scanner;
import objectworks.controlador.ClienteController;
import objectworks.controlador.ConsultasController;
import objectworks.controlador.ReservaController;
import objectworks.controlador.HabitacionController;
import objectworks.controlador.HotelController;

public class VistaPrincipal {
    private final ClienteController clienteController;
    private final ReservaController reservaController;
    private final HotelController hotelController;
    private final HabitacionController habitacionController;
    private final ConsultasController consultasController;

    private Scanner teclado = new Scanner(System.in);


    public VistaPrincipal(ClienteController clienteController, ReservaController reservaController,
                          HotelController hotelController, HabitacionController habitacionController,
                          ConsultasController consultasController) {
        this.clienteController = clienteController;
        this.hotelController = hotelController;
        this.habitacionController = habitacionController;
        this.reservaController = reservaController;
        this.consultasController = consultasController;
    }

    public void mostrarErrorConexion() {
        System.out.println("No se ha podido conectar con la base de datos MySQL.");
        System.out.println("Comprueba que MySQL esta arrancado, que has ejecutado sql/objectworks_bd.sql");
        System.out.println("y que el fichero db.properties tiene tu usuario y contrasena.");
    }

    public void inicio() {
        boolean salir = false;
        do {
            System.out.println("\n=== ObjectWorks - Gestion de Hoteles ===");
            System.out.println("1. Gestion de clientes");
            System.out.println("2. Gestion de hoteles");
            System.out.println("3. Gestion de habitaciones");
            System.out.println("4. Gestion de reservas");
            System.out.println("5. Consulta de reservas");
            System.out.println("0. Salir");
            System.out.print("Elige una opcion: ");
            String opcion = teclado.nextLine();

            if (opcion.equals("1")) {
                new GestionClientesVista(clienteController, reservaController, teclado).menu();
            } else if (opcion.equals("2")) {
                new GestionHotelesVista(hotelController,habitacionController, teclado).menu();
            } else if (opcion.equals("3")) {
                new GestionHabitacionesVista(habitacionController, teclado).menu();
            } else if (opcion.equals("4")) {
                new GestionReservasVista(reservaController, clienteController, hotelController, habitacionController, teclado).menu();
            } else if (opcion.equals("5")) {
                new GestionConsultasVista(consultasController, teclado).menu();
            } else if (opcion.equals("0")) {
                salir = true;
            } else {
                System.out.println("Opcion no valida");
            }
        } while (!salir);
        System.out.println("Hasta luego");
    }
}