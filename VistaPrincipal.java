package objectworks.vista;

import java.util.Scanner;
import objectworks.controlador.ClienteController;
import objectworks.controlador.Controlador;
import objectworks.modelo.*;
import objectworks.modelo.enums.CategoriaHabitacion;
import objectworks.modelo.exceptions.DatoObligatorioException;
import objectworks.modelo.exceptions.FechasInvalidasException;
import objectworks.modelo.exceptions.HotelNoEncontradoException;

import java.time.LocalDate;

public class VistaPrincipal {
    private final ClienteController clienteController;
    // TODO: cada parte sustituye este Controlador antiguo por su propio controlador
    // (HotelController, HabitacionController, ReservaController, ConsultaReservaController)
    private Controlador controlador;
    private Scanner teclado = new Scanner(System.in);

    public VistaPrincipal(ClienteController clienteController) {
        this.clienteController = clienteController;
        this.controlador = new Controlador();
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
                new GestionClientesVista(clienteController, controlador, teclado).menu();
            } else if (opcion.equals("2")) {
                new GestionHotelesVista(controlador, teclado).menu();
            } else if (opcion.equals("3")) {
                new GestionHabitacionesVista(controlador, teclado).menu();
            } else if (opcion.equals("4")) {
                new GestionReservasVista(controlador, clienteController, teclado).menu();
            } else if (opcion.equals("5")) {
                new GestionConsultasVista(controlador, teclado).menu();
            } else if (opcion.equals("0")) {
                salir = true;
            } else {
                System.out.println("Opcion no valida");
            }
        } while (!salir);
        System.out.println("Hasta luego");
    }
}